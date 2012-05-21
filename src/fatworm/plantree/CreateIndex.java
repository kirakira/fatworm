package fatworm.plantree;

import fatworm.record.RecordFile;
import fatworm.record.Schema;
import fatworm.util.Util;

public class CreateIndex extends Command{
    public CreateIndex(String name) {
        super(name);
    }
    
    public CreateIndex(String tableName, String columnName) {
        super(tableName);
        this.tableName = tableName.toLowerCase();
        this.columnName = columnName.toLowerCase();
    }
    String tableName;
    String columnName;
    @Override
    public void execute() {
        RecordFile rf = Util.getStorageManager().getTable(tableName);
        Schema schema = rf.getSchema();
    //    if (schema.type(columnName) == java.sql.Types.VARCHAR)
            rf.createIndex(columnName);
    }
}
