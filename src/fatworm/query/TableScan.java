package fatworm.query;

import java.util.Collection;
import java.util.LinkedList;

import fatworm.dataentity.DataEntity;
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

    public Collection<String> fields() {
        return schema.fields();
    }

    public Collection<String> columns() {
        LinkedList<String> result = new LinkedList<String>();
        for(String f:schema.fields()) {
            result.add(tableName + f);
        }
        return result;
    }

	@Override
	public DataEntity getField(String fldname) {
		return iter.getField(fldname);
	}

	@Override
	public DataEntity getFirstColumn() {
		// TODO Auto-generated method stub
		return null;
	}
}