package fatworm.plantree;

import java.util.Map;

import fatworm.dataentity.DataEntity;
import fatworm.record.Schema;

abstract public class InsertCommand extends Command{
	public InsertCommand(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public abstract Map<String, DataEntity> getTupleMap(Schema schema); 

}
