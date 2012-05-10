package fatworm.record;

import java.util.Map;

import fatworm.dataentity.DataEntity;

public interface RecordFile {
    public void beforeFirst();
    public boolean next();
    public DataEntity getField(String field);
    public DataEntity getFieldByIndex(int index);
    public boolean hasField(String field);
    public boolean update(Map<String, DataEntity> tuple) throws java.io.IOException;
    public boolean insert(Map<String, DataEntity> tuple) throws java.io.IOException;
    public void delete() throws java.io.IOException;
    public Schema getSchema();
}
