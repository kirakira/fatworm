package fatworm.absyn;

import java.util.HashSet;
import java.util.Set;

import fatworm.plantree.Node;
import fatworm.query.Env;
import fatworm.query.Scan;
import fatworm.util.Util;
import fatworm.dataentity.DataEntity;

public class CompareAllExpr extends BoolExpr{
	Value val;
	Node query;
	String cop;
	public CompareAllExpr(Value val, Node query, String cop){
		this.val = val;
		this.query = query;
		this.cop = cop;
	}

    public boolean satisfiedBy(Env env) {
        DataEntity left = val.getValue(env);
        Scan rightscan = Util.getQueryPlanner().createQueryPlan(query,env).open();
        rightscan.beforeFirst();
        while(rightscan.next()) {
            if(!Compare.compare(left, rightscan.getColumnByIndex(0), cop))
                return false;
        }
        return true;
    }
    
    public Set<String> dumpUsefulColumns() {
    	Set<String> result = new HashSet<String>();
    	result.addAll(val.dumpUsefulColumns());
    	result.addAll(query.dumpUsefulColumns());
    	return result;
    }


}
