package fatworm.query;

import fatworm.dataentity.DataEntity;

public interface Env {
    DataEntity getValue(String tableName, String columnName);
    DataEntity getValue(String columnName);
}