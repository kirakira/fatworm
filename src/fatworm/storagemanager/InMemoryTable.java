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

public class InMemoryTable implements RecordFile {

	Schema schema;
	ListIterator<DataEntity[]> iter;
	DataEntity[] now;
	List<DataEntity[]> table = new LinkedList<DataEntity[]>();
	public InMemoryTable(Schema schema) {
		this.schema = schema;
		iter = table.listIterator();
	}
	@Override
	public void beforeFirst() {
		iter = table.listIterator();
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
		return now[schema.index(field)];
	}

	@Override
	public DataEntity getFieldByIndex(int index) {
		return now[index];
	}

	@Override
	public boolean hasField(String field) {
		return schema.hasField(field);
	}

	@Override
	public boolean update(Map<String, DataEntity> tuple) {
		for(Entry<String, DataEntity> newdata: tuple.entrySet()) {
			if (schema.index(newdata.getKey()) >= 0)
				now[schema.index(newdata.getKey())] = newdata.getValue();
			else 
				return false;
		}
		return true;
	}

	@Override
	public boolean insert(Map<String, DataEntity> tuple) {
		DataEntity[] x = new DataEntity[schema.columnCount()];
		for (int i = 0; i < x.length; i++)
			x[i] = new NullDataEntity();
		for(Entry<String, DataEntity> newdata: tuple.entrySet()) {
			if (schema.index(newdata.getKey()) >= 0)
				x[schema.index(newdata.getKey())] = newdata.getValue();
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
		return schema;
	}

}
