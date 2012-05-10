package fatworm.query;

import fatworm.dataentity.DataEntity;

public interface Env {
    DataEntity getValue(String colname);
    void putValue(String colname, DataEntity data);
    void beginScope();
    void endScope();
}