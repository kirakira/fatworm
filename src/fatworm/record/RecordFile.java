package fatworm.record;

import java.util.Map;

import fatworm.dataentity.DataEntity;

public interface RecordFile{
    //    public void close();
    public void beforeFirst();
    public boolean next();
    public DataEntity getField(String field);
    public DataEntity getFieldByIndex(int index);
    public boolean hasField(String field);
    public void update(Map<String, DataEntity> tuple);
    public void delete();
    //    public Collection<String> getFields();
    public Schema getSchema();
}
