package fatworm.plantree;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import fatworm.absyn.ConstValue;
import fatworm.absyn.Value;
import fatworm.dataentity.DataEntity;
import fatworm.record.Schema;

public class FieldInsert extends InsertCommand {

	public FieldInsert(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public Map<String, Value> assigns = new HashMap<String, Value>();
	@Override
	public Map<String, DataEntity> getTupleMap(Schema schema) {
		Map<String, DataEntity> result = new HashMap<String, DataEntity>();
		for(Entry<String, Value> assign: assigns.entrySet()) {
			result.put(assign.getKey(), assign.getValue().getValue(null).toType(schema.type(assign.getKey())));
		}
		return result;
	}
	@Override
	public void execute() {
		
	}

}
