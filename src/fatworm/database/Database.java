package fatworm.database;

import fatworm.planner.QueryPlanner;

public class Database {
    QueryPlanner queryPlanner;
    static Database instance;
    public  QueryPlanner getQueryPlanner() {
        return queryPlanner;
    }
    
    public static Database getInstance() {
    	return instance;
    }
}