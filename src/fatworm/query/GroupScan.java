package fatworm.query;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import fatworm.dataentity.DataEntity;
import fatworm.record.RecordIterator;
import fatworm.util.Util;

public class GroupScan implements Scan {

	GroupContainer container;
	String keyName;
	Scan scan;
	Set<String> funcSet;
	Map<String, String> realname;
	
	public GroupScan (Scan scan, String keyName, Set<String> funcSet) {
		this.funcSet = funcSet;
		this.keyName = keyName; 
		this.scan = scan;
	}
	@Override
	public void beforeFirst() {
		if (!scan.hasColumn(keyName))
		    this.keyName = getRealName(keyName);
		container = Util.getGroupContainer(this.keyName, funcSet);
		scan.beforeFirst();
		while(scan.next()) {
			container.update(scan);
		}
		container.finish();
		container.beforeFirst();
		scan.beforeFirst();
	}

	@Override
	public boolean next() {
		if (container.next()) {
			//dirty hack, will be slow;
			scan.beforeFirst();
			while(scan.next()) {
				DataEntity keyValue = scan.getColumn(keyName);
				if ( keyValue.toString().equals(container.getKeyValue().toString())
						&& !keyValue.isNull() )
					break;
			}
			return true;
		}
		return false;
	}

	@Override
	public DataEntity getField(String fldname) {
		if (keyName.equals(fldname) || Util.hasField(keyName, fldname))
			return container.getKeyValue();
		return scan.getField(fldname);
	}

	@Override
	public boolean hasField(String fldname) {
		if (keyName.equals(fldname) || Util.hasField(keyName, fldname))
			return true;
		return scan.hasField(fldname);

	}

	@Override
	public DataEntity getColumn(String colname) {
		return scan.getColumn(colname);
	}

	@Override
	public boolean hasColumn(String colname) {
		return scan.hasColumn(colname);
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
		return scan.getColumnByIndex(index);
	}

	@Override
	public int getNumberOfColumns() {
		return scan.getNumberOfColumns();
	}

	@Override
	public int indexOfField(String field) {
		return scan.indexOfField(field);
	}
	
	public int indexOfColumn(String column) {
		return scan.indexOfColumn(column);
	}

	@Override
	public int type(String colname) {
		return scan.type(colname);
	}

	@Override
	public int type(int index) {
		return scan.type(index);
	}

	@Override
	public String fieldName(int index) {
		return scan.fieldName(index);
	}

	@Override
	public String columnName(int index) {
		return scan.columnName(index);
	}

	@Override
	public RecordIterator getRecordFile() {
		return null;
	}

	@Override
	public DataEntity getFunctionValue(String func) {
		return container.getFunctionValue(func);
	}

	@Override
	public boolean hasFunctionValue(String func) {
		return funcSet.contains(func);
	}
	
		@Override
	public DataEntity getOrderKey(String key) {
		return getColumn(key);
	}
        @Override
        public boolean hasIndex(String colname) {
            // TODO Auto-generated method stub
            return false;
        }
        @Override
        public RecordIterator getIndex(String colname, DataEntity right, String cop) {
            // TODO Auto-generated method stub
            return null;
        }
        public String getRealName(String alias) {
            return realname.get(alias) == null ? alias: realname.get(alias);
        }
        @Override
        public void setRealName(Map<String, String> map) {
            scan.setRealName(map);
            realname = map;
        }			
}
