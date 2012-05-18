package fatworm.storagemanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import fatworm.dataentity.DataEntity;
import fatworm.dataentity.NullDataEntity;
import fatworm.record.RecordFile;
import fatworm.record.Schema;
import fatworm.record.RecordIterator;

public class InMemoryTableWithIter implements RecordFile {


	ListIterator<DataEntity[]> iter;
	DataEntity[] now;
	InMemoryTable table;
	public InMemoryTableWithIter(InMemoryTable table) {
		this.table = table;
		iter = table.table.listIterator();
	}
	@Override
	public void beforeFirst() {
		iter = table.table.listIterator();
	}

	@Override
	public boolean next() {
		if(iter.hasNext()) {
			now = iter.next();
			return true;
		}
		return false;
	}

	@Override
	public DataEntity getField(String field) {
		return now[table.schema.index(field)];
	}

	@Override
	public DataEntity getFieldByIndex(int index) {
		return now[index];
	}

	@Override
	public boolean hasField(String field) {
		return table.schema.hasField(field);
	}

	@Override
	public boolean update(Map<String, DataEntity> tuple) {
		for(Entry<String, DataEntity> newdata: tuple.entrySet()) {
			if (table.schema.index(newdata.getKey()) >= 0)
				now[table.schema.index(newdata.getKey())] = newdata.getValue();
			else 
				return false;
		}
		return true;
	}

	@Override
	public boolean insert(Map<String, DataEntity> tuple) {
		DataEntity[] x = new DataEntity[table.schema.columnCount()];
		for (int i = 0; i < x.length; i++)
			x[i] = new NullDataEntity();
		for(Entry<String, DataEntity> newdata: tuple.entrySet()) {
			if (table.schema.index(newdata.getKey()) >= 0)
				x[table.schema.index(newdata.getKey())] = newdata.getValue();
			else 
				return false;
		}
		iter.add(x);
		return true;
		
	}

	@Override
	public void delete() {
		iter.remove();
	}

	@Override
	public Schema getSchema() {
		return table.schema;
	}

    public DataEntity min(String col) {
        return null;
    }

    public DataEntity max(String col) {
        return null;
    }

    public RecordIterator scan() {
        return null;
    }

    public void createIndex(String col) {
    }

    public void dropIndex(String col) {
    }

    public RecordIterator indexEqual(String col, DataEntity value) {
        return null;
    }

    public RecordIterator indexLessThan(String col, DataEntity value) {
        return null;
    }

    public RecordIterator indexLessThanEqual(String col, DataEntity value) {
        return null;
    }

    public RecordIterator indexGreaterThan(String col, DataEntity value) {
        return null;
    }

    public RecordIterator indexGreaterThanEqual(String col, DataEntity value) {
        return null;
    }
    @Override
    public DataEntity[] tuple() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean insert(DataEntity[] tuple) {
        // TODO Auto-generated method stub
        return false;
    }
}
