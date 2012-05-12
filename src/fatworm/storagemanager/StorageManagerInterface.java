package fatworm.storagemanager;

import fatworm.record.RecordFile;
import fatworm.record.Schema;

public interface StorageManagerInterface {
	public RecordFile getTable(String tablename);
	public RecordFile insertTable(String tablename, Schema schema);
	public void dropTable(String name);
	public boolean useDatabase(String name);
	public boolean dropDatabase(String name);
	public boolean createDatabase(String name);
    public void save();
    public void setPath(String path);
}
