package fatworm.query;

public class TablePlan extends QueryPlan{
    
    String tableName;
    public TablePlan(String name) {
        tableName = name;
    }
    public Scan open() {
        return new TableScan(tableName);
    }
}