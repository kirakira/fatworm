package fatworm.util;

import fatworm.planner.QueryPlanner;
import fatworm.database.*;

public class Util
{
    public static QueryPlanner getQueryPlanner() {
        return Database.getInstance().getQueryPlanner();
    }
}