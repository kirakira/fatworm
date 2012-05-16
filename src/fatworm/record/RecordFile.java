package fatworm.record;

import java.util.Map;

import fatworm.dataentity.DataEntity;

public interface RecordFile {
    // deprecated; use scan().beforeFirst() instead
    public void beforeFirst();
    // deprecated; use scan().next() instead
    public boolean next();
    // deprecated; use scan().getField(String) instead
    public DataEntity getField(String field);
    // deprecated; use scan().getField(int) instead
    public DataEntity getFieldByIndex(int index);
    // deprecated; use scan().getTuple() instead
    public DataEntity[] tuple();

    // deprecated; use scan().update(Map<String, DataEntity>) instead
    public boolean update(Map<String, DataEntity> tuple);
    // deprecated; use scan().remove() instead
    public void delete();


    public Schema getSchema();
    // deprecated; use getSchema().hasField(String) instead
    public boolean hasField(String field);

    public boolean insert(Map<String, DataEntity> tuple);
    public boolean insert(DataEntity[] tuple);

    // scan the whole table
    public Iterator scan();

    // index manipulation
    public void createIndex(String col);
    public void dropIndex(String col);

    // index search
    public Iterator indexEqual(String col, DataEntity value);
    public Iterator indexLessThan(String col, DataEntity value);
    public Iterator indexLessThanEqual(String col, DataEntity value);
    public Iterator indexGreaterThan(String col, DataEntity value);
    public Iterator indexGreaterThanEqual(String col, DataEntity value);

    public DataEntity max(String col);
    public DataEntity min(String col);
}
