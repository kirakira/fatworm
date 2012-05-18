package fatworm.storage;

import fatworm.record.RecordIterator;
import fatworm.dataentity.*;
import fatworm.record.Schema;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CellListIterator implements RecordIterator {
    private Schema schema;
    private IOHelper io;
    private int colindex;
    private DataEntity value;
    private List<Integer> list;

    private Iterator<Integer> listIter;
    private RecordIterator cellIter;

    public CellListIterator(Schema schema, IOHelper io, int colindex, DataEntity value, List<Integer> list) {
        this.schema = schema;
        this.io = io;
        this.colindex = colindex;
        this.value = value;
        this.list = list;

        listIter = null;
        cellIter = null;
    }

    public void beforeFirst() {
        listIter = new HashSet<Integer>(list).iterator();
        cellIter = null;
    }

    public boolean next() {
        if (cellIter == null || !cellIter.next()) {
            while (listIter.hasNext()) {
                int cellBlock = listIter.next().intValue();
                Cell cell = Cell.load(io, cellBlock);
                cellIter = new CellIterator(schema, cell, colindex, value);
                cellIter.beforeFirst();
                if (cellIter.next())
                    return true;
            }
            return false;
        } else
            return true;
    }

    public DataEntity getField(String col) {
        return cellIter.getField(col);
    }

    public DataEntity getField(int col) {
        return cellIter.getField(col);
    }

    public DataEntity[] getTuple() {
        return cellIter.getTuple();
    }

    public boolean update(Map<String, DataEntity> map) {
        System.err.println("Calling update of CellListIterator");
        return false;
    }

    public void remove() {
        System.err.println("Calling remove of CellListIterator");
    }
}
