package fatworm.util;

import fatworm.planner.QueryPlanner;
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
}