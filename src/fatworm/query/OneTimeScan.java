package fatworm.query;

import java.util.Collection;

import fatworm.dataentity.DataEntity;
import fatworm.record.RecordFile;

public class OneTimeScan implements Scan {

	boolean before = false;
	boolean hasElement;
	
	public OneTimeScan(boolean hasElement) {
		this.hasElement = hasElement;
	}
	@Override
	public void beforeFirst() {
		before = true;
	}

	@Override
	public boolean next() {
		if (hasElement && before) {
			before = false;
			return true;
		}
		return false;
	}

	@Override
	public DataEntity getField(String fldname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasField(String fldname) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DataEntity getColumn(String colname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasColumn(String colname) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<String> fields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> columns() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataEntity getColumnByIndex(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfColumns() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int indexOfColumn(String column) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public int indexOfField(String field) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public int type(String colname) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public int type(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String fieldName(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String columnName(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordFile getRecordFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataEntity getFunctionValue(String func) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasFunctionValue(String func) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DataEntity getOrderKey(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
