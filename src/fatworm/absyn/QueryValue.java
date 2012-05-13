package fatworm.absyn;

import java.util.Map;
import java.util.Set;

import fatworm.dataentity.DataEntity;
import fatworm.dataentity.NullDataEntity;
import fatworm.plantree.Node;
import fatworm.query.Env;
import fatworm.query.Scan;
import fatworm.util.Util;

public class QueryValue extends Value {

	
	public QueryValue(Node query) {
		this.query = query; 
	}
	Node query;
	Scan scan;
	@Override
	public DataEntity getValue(Env env) {
		scan = Util.getQueryPlanner().createQueryPlan(query, env).open();
		if (scan.next()) {
			return scan.getColumnByIndex(0);
		}
		return new NullDataEntity();
	}

	@Override
	public Set<String> dumpUsefulColumns() {
		return query.dumpUsefulColumns();
	}

	@Override
	public int getType(Map<String, Integer> typemap) {
		//XXX
		return 0;
	}

}
