package fatworm.plantree;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import fatworm.absyn.ConstValue;
import fatworm.dataentity.DataEntity;
import fatworm.record.Schema;

public class FieldInsert extends InsertCommand {

	public FieldInsert(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	Map<String, ConstValue> assigns;
	@Override
	public Map<String, DataEntity> getTupleMap(Schema schema) {
		Map<String, DataEntity> result = new HashMap<String, DataEntity>();
		for(Entry<String, ConstValue> assign: assigns.entrySet()) {
			result.put(assign.getKey(), assign.getValue().getValue(null).toType(schema.type(assign.getKey())));
		}
		return result;
	}
	@Override
	public void execute() {
		
	}

}
