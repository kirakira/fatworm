package fatworm.storagemanager;

import java.util.HashMap;
import java.util.Map;

import fatworm.record.RecordFile;
import fatworm.record.Schema;

public class MemoryStorageManager implements StorageManagerInterface {

	static Map<String, MemoryStorageManager> dbms = new HashMap<String, MemoryStorageManager>();
	static MemoryStorageManager now;
	Map<String, InMemoryTable> database = new HashMap<String, InMemoryTable>();
	
	
	@Override
	public RecordFile getTable(String tablename) {
		if (this == now)
			return database.get(tablename);
		else 
			return now.getTable(tablename);
	}

	@Override
	public RecordFile insertTable(String tablename, Schema schema) {
		if (this == now) {
			database.put(tablename, new InMemoryTable(schema));
			return database.get(tablename);
		}
		else 
			return now.insertTable(tablename, schema);
			
	}

	@Override
	public void dropTable(String name) {
		if (this == now)
			database.remove(name);
		else
			now.dropDatabase(name);
	}

	@Override
	public boolean useDatabase(String name) {
		now = dbms.get(name);
		return now != null;
	}

	@Override
	public boolean dropDatabase(String name) {
		return dbms.remove(name) == null;
	}

	@Override
	public boolean createDatabase(String name) {
		if (dbms.get(name) == null) {
			dbms.put(name, new MemoryStorageManager());
			return true;
		}
		return false;
	}

	@Override
	public void save() {
	}

}
