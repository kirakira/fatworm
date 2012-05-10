package fatworm.plantree;

import java.util.HashMap;
import java.util.Map;

import fatworm.dataentity.DataEntity;
import fatworm.query.Scan;
import fatworm.record.Schema;

public class QueryInsert extends InsertCommand{

	public QueryInsert(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public Node query;
	@Override
	public Map<String, DataEntity> getTupleMap(Schema schema) {
		return null;
	}

	public Map<String, DataEntity> getTupleMap(Schema schema, Scan scan) {
		Map<String, DataEntity> result = new HashMap<String, DataEntity>();
		for(int i = 0; i < scan.getNumberOfColumns(); i++) {
			result.put(schema.name(i), scan.getColumnByIndex(i).toType(schema.type(i)));
		}
		return result;		
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}
}
