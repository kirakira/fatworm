package fatworm.storagemanager;


import java.util.LinkedList;
import java.util.List;

import fatworm.dataentity.DataEntity;
import fatworm.record.Schema;

public class InMemoryTable {
	
	public InMemoryTable(Schema schema) {
		this.schema = schema;
	}

	Schema schema;
	List<DataEntity[]> table = new LinkedList<DataEntity[]>();
}
