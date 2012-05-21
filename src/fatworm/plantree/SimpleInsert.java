package fatworm.plantree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fatworm.absyn.ConstDefault;
import fatworm.absyn.ConstNull;
import fatworm.absyn.ConstValue;
import fatworm.absyn.Value;
import fatworm.dataentity.DataEntity;
import fatworm.record.Schema;

public class SimpleInsert extends InsertCommand {

	public SimpleInsert(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public ArrayList<Value> values;
	@Override
	public Map<String, DataEntity> getTupleMap(Schema schema) {
		Map<String, DataEntity> result = new HashMap<String, DataEntity>();
		for(int i = 0; i < values.size(); i++) {
			Value value = values.get(i);
			if (value instanceof ConstDefault) {
				continue;
			}
			if (value instanceof ConstNull) {
			    continue;
			}
			result.put(schema.name(i), value.getValue(null).toType(schema.type(i)));
		}
		return result;
	}
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}
}
