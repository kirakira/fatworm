package fatworm.query;

import java.util.Collection;
import java.util.LinkedList;

import fatworm.dataentity.DataEntity;
import fatworm.record.Iterator;
import fatworm.record.RecordFile;
import fatworm.record.Schema;
import fatworm.util.Util;

public class TableScan implements Scan{
    String tableName;
    Schema schema;
    RecordFile iter;
    public TableScan(String name) {
        this.tableName = name;
        this.iter = Util.getTable(name);
        this.schema = iter.getSchema();
    }
    
    public void beforeFirst() {
        iter.beforeFirst();
    }
    
    public boolean next() {
        return iter.next();
    }

    public boolean hasField(String field) {
        return schema.hasField(field);
    }

    public boolean hasColumn(String column) {
        if (Util.isFieldSuffix(column)) {
            return tableName.equals(Util.getColumnTableName(column)) && schema.hasField(Util.getColumnFieldName(column));
        }
        if (Util.isSimpleColumn(column)) {
            return schema.hasField(column);
        }
        return false;
    }

	@Override
	public DataEntity getField(String fldname) {
		return iter.getField(fldname);
	}


    public DataEntity getColumn(String column) {
        if (Util.isFieldSuffix(column)) {

            if ( tableName.equals(Util.getColumnTableName(column)))
                return getField(Util.getColumnFieldName(column));
        }
        if (Util.isSimpleColumn(column)) {
            return getField(column);
        }
        return null;
    }

    public Collection<String> fields() {
        return schema.fields();
    }

    public Collection<String> columns() {
        LinkedList<String> result = new LinkedList<String>();
        for(String f:schema.fields()) {
            result.add(Util.makeColumnName(tableName,f));
        }
        return result;
    }


	@Override
	public DataEntity getColumnByIndex(int index) {
		return iter.getFieldByIndex(index);
	}
	
	public int getNumberOfColumns() {
		return fields().size();
	}

	public int indexOfField(String field) {
		return schema.index(field);
	}
	
	@Override
	public int indexOfColumn(String colname) {
		if (Util.isFieldSuffix(colname)) {
            if ( tableName.equals(Util.getColumnTableName(colname)))
                return schema.index(Util.getColumnFieldName(colname));
        }
        if (Util.isSimpleColumn(colname)) {
            return schema.index(colname);
        }
		return -1;
	}

	@Override
	public int type(String colname) {
		return type(indexOfField(colname));
	}

	@Override
	public int type(int index) {
		return schema.type(index);
	}

	@Override
	public String fieldName(int index) {
		return schema.name(index);
	}

	@Override
	public String columnName(int index) {
		return tableName + "." + fieldName(index);
	}

	@Override
	public RecordFile getRecordFile() {
		return iter;
	}

	@Override
	public DataEntity getFunctionValue(String func) {
		// no function from table;
		return null;
	}
	
	public boolean hasFunctionValue(String func) {
		return false;
	}	
	
	@Override
	public DataEntity getOrderKey(String key) {
		return getColumn(key);
	}

    @Override
    public boolean hasIndex(String colname) {
        return true;
    }

    @Override
    public Iterator getIndex(String colname, DataEntity right, String cop) {
        if (cop.equals("EQ"))
            return iter.indexEqual(colname, right);
        if (cop.equals("LE"))
            return iter.indexLessThanEqual(colname, right);
        if (cop.equals("LT"))
            return iter.indexLessThan(colname, right);
        if (cop.equals("GE"))
            return iter.indexGreaterThanEqual(colname, right);
        if (cop.equals("GT"))
            return iter.indexGreaterThan(colname, right);
        return null;
    }
}
