package fatworm.storagemanager;

import java.util.Map;

import fatworm.record.RecordFile;
import fatworm.record.Schema;

public class MemoryStorageManager implements StorageManagerInterface {

	Map<String, InMemoryTable> database;
	
	@Override
	public RecordFile getTable(String tablename) {
		return database.get(tablename);
	}

	@Override
	public void insertTable(String tablename, Schema schema) {
		database.put(tablename, new InMemoryTable(schema));
	}

}
