package fatworm.storage;

import fatworm.record.RecordIterator;
import fatworm.dataentity.*;
import fatworm.record.Schema;
import fatworm.storage.bplustree.BPlusTree.NodeIterator;
import fatworm.storage.bplustree.BPlusTree.Pair;
import fatworm.util.Predicate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IndexIterator implements RecordIterator {
    private Schema schema;
    private IOHelper io;
    private int colindex;
    private Predicate<DataEntity> predicate;
    private DataAdapter adapter;

    private NodeIterator nodeIter;
    private RecordIterator cellListIter;

    public IndexIterator(Schema schema, IOHelper io, int colindex, NodeIterator nodeIter, Predicate<DataEntity> predicate, DataAdapter da) {
        this.schema = schema;
        this.io = io;
        this.colindex = colindex;
        this.nodeIter = nodeIter;
        this.predicate = predicate;
        this.adapter = da;

        cellListIter = null;
    }

    public void beforeFirst() {
        nodeIter.beforeFirst();
        cellListIter = null;
    }

    public boolean next() {
        if (cellListIter == null || !cellListIter.next()) {
            while (nodeIter.hasNext()) {
                Pair pair = nodeIter.next();
                DataEntity value = adapter.getData(pair.key());
                if (predicate.apply(value) == false)
                    return false;
                cellListIter = new CellListIterator(schema, io, colindex, value, pair.values());
                cellListIter.beforeFirst();
                if (cellListIter.next())
                    return true;
            }
            return false;
        } else
            return true;
    }

    public DataEntity getField(String col) {
        return cellListIter.getField(col);
    }

    public DataEntity getField(int col) {
        return cellListIter.getField(col);
    }

    public DataEntity[] getTuple() {
        return cellListIter.getTuple();
    }

    public boolean update(Map<String, DataEntity> map) {
        System.err.println("Calling update of IndexIterator");
        return false;
    }

    public void remove() {
        System.err.println("Calling remove of IndexIterator");
    }
}
