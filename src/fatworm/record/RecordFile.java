package fatworm.record;

import java.util.Map;

import fatworm.dataentity.DataEntity;

public interface RecordFile {
    public void beforeFirst();
    public boolean next();
    public DataEntity getField(String field);
    public DataEntity getFieldByIndex(int index);
    public boolean hasField(String field);
    public DataEntity[] tuple();

    public boolean update(Map<String, DataEntity> tuple);
    public boolean insert(Map<String, DataEntity> tuple);
    public boolean insert(DataEntity[] tuple);
    public void delete();
    public Schema getSchema();
}
