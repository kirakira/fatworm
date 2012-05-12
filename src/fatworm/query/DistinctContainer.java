package fatworm.query;

import fatworm.dataentity.DataEntity;

public abstract class DistinctContainer {
	abstract public void update(Scan scan);
	abstract public void finish();
	abstract public DataEntity getColumnByIndex(int index);
	abstract public boolean next();
	abstract public void beforeFirst();
}
