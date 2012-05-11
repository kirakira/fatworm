package fatworm.storagemanager;

import java.util.HashMap;
import java.util.Map;

import fatworm.record.RecordFile;
import fatworm.record.Schema;

public class MemoryStorageManager implements StorageManagerInterface {

	Map<String, InMemoryTable> database = new HashMap<String, InMemoryTable>();
	
	@Override
	public RecordFile getTable(String tablename) {
		return database.get(tablename);
	}

	@Override
	public RecordFile insertTable(String tablename, Schema schema) {
		database.put(tablename, new InMemoryTable(schema));
		return database.get(tablename);
	}

	@Override
	public void dropTable(String name) {
		database.remove(name);
	}

	@Override
	public boolean useDatabase(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dropDatabase(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createDatabase(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

}
