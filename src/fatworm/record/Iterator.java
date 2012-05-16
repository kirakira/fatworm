package fatworm.record;

import fatworm.dataentity.DataEntity;

import java.util.Map;

public interface Iterator {
    public void beforeFirst();
    public boolean next();

    public DataEntity getField(int index);
    public DataEntity getField(String fldname);
    public DataEntity[] getTuple();

    public boolean update(Map<String, DataEntity> map);
    public void remove();
}
