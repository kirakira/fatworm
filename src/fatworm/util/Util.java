package fatworm.util;

import fatworm.planner.QueryPlanner;
import fatworm.query.Env;
import fatworm.query.SimpleEnv;
import fatworm.record.RecordFile;
import fatworm.database.*;

public class Util
{
    public static QueryPlanner getQueryPlanner() {
        return Database.getInstance().getQueryPlanner();
    }

	public static RecordFile getTable(String name) {
		return Database.getInstance().getStorageManager().openTable(name);
	}

	public static boolean isFieldSuffix(String column) {
		return (!column.contains("(") && column.contains("."));
	}

	public static Object getColumnTableName(String column) {
		return column.substring(0, column.indexOf("."));
	}

	public static boolean isSimpleColumn(String column) {
		return (!column.contains("(") && !column.contains("."));	
	}

	public static String getColumnFieldName(String column) {
		return column.substring(column.indexOf(".")+1);	
	}

	public static Env getEmptyEnv() {
		return new SimpleEnv();
	}

	public static boolean isFunction(String column) {
		return column.startsWith("AVG(") 
				|| column.startsWith("COUNT(") 
				|| column.startsWith("MAX(")
				|| column.startsWith("MIN(") 
				|| column.startsWith("SUM(");
	}
}