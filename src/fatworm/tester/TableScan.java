package fatworm.tester;

import fatworm.query.Scan;
import java.util.Collection;

import fatworm.dataentity.DataEntity;
import fatworm.record.RecordFile;

public class TableScan implements Scan {
    private RecordFile table;

    public TableScan(RecordFile record) {
        this.table = record;
    }

    public void beforeFirst() {
        table.beforeFirst();
    }

    public boolean next() {
        return table.next();
    }

    public DataEntity getField(String fldname) {
        return table.getField(fldname);
    }

    public boolean hasField(String fldname) {
        return table.hasField(fldname);
    }

    public DataEntity getColumn(String colname) {
        return getField(colname);
    }

    public boolean hasColumn(String colname) {
        return hasField(colname);
    }

    public Collection<String> fields() {
        return table.getSchema().fields();
    }

    public Collection<String>  columns() {
        return fields();
    }

    public DataEntity getColumnByIndex(int index) {
        return table.getFieldByIndex(index);
    }

    public int getNumberOfColumns() {
        return table.getSchema().columnCount();
    }

    public int indexOfColumn(String column) {
        return table.getSchema().index(column);
    }

    public int indexOfField(String field) {
        return indexOfColumn(field);
    }

    public int type(String colname) {
        return table.getSchema().type(colname);
    }

    public int type(int index) {
        return table.getSchema().type(index);
    }

    public String fieldName(int index) {
        return table.getSchema().name(index);
    }

    public String columnName(int index) {
        return fieldName(index);
    }

    public RecordFile getRecordFile() {
        return table;
    }

    public DataEntity getFunctionValue(String func) {
        return null;
    }

    public boolean hasFunctionValue(String func) {
        return false;
    }

    public DataEntity getOrderKey(String key) {
        return null;
    }
}
