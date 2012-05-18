package fatworm.storage;

import fatworm.record.*;
import fatworm.dataentity.*;

import java.util.Map;

public class CellIterator implements RecordIterator {
    private Cell cell;
    private int current, colindex;
    private DataEntity value;
    private Schema schema;

    public CellIterator(Schema schema, Cell cell, int colindex, DataEntity value) {
        this.schema = schema;
        this.cell = cell;
        this.colindex = colindex;
        this.value = value;
        current = -1;
    }

    public void beforeFirst() {
        current = -1;
    }

    public boolean next() {
        ++current;
        while (current < cell.tupleCount()) {
            DataEntity x = cell.get(current).tuple()[colindex];
            if (!x.isNull() && x.compareTo(value) == 0)
                return true;
            ++current;
        }
        return false;
    }

    public DataEntity getField(int index) {
        return getTuple()[index];
    }

    public DataEntity getField(String colname) {
        int colindex = schema.index(colname);
        return getField(colindex);
    }

    public DataEntity[] getTuple() {
        return cell.get(current).tuple();
    }

    public boolean update(Map<String, DataEntity> map) {
        System.err.println("Calling update of CellIterator");
        return false;
    }

    public void remove() {
        System.err.println("Calling remove of CellIterator");
    }
}
