package fatworm.query;

public interface Env {
    Object getValue(String tableName, String columnName);
    Object getValue(String columnName);
}