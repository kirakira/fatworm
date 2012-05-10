package fatworm.database;

import fatworm.planner.QueryPlanner;
import fatworm.storage.StorageManager;
import fatworm.storagemanager.StorageManagerInterface;

public class Database {
    QueryPlanner queryPlanner;
    static Database instance;
    StorageManagerInterface storageManager;
    public  QueryPlanner getQueryPlanner() {
        return queryPlanner;
    }
    
    public static Database getInstance() {
    	return instance;
    }
    
    public StorageManagerInterface getStorageManager() { 
    	return storageManager;
    }
}