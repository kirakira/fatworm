package fatworm.storage;

import fatworm.record.RecordFile;
import fatworm.record.Schema;
import fatworm.record.Iterator;
import fatworm.storage.bucket.Bucket;
import fatworm.util.ByteLib;
import fatworm.dataentity.*;

import static java.sql.Types.*;
import java.util.Map;

public class Table implements RecordFile {
    private IOHelper io;
    private String name;
    private SchemaOnDisk schema;

    private Bucket head;

    private int front, rear;
    private int capacity;

    private Iterator scanIter;

    private Table(IOHelper io, String name, int schema) {
        this.io = io;
        this.name = name;
        if (schema == 0)
            this.schema = null;
        else
            this.schema = SchemaOnDisk.load(io, schema);
        front = 0;
        rear = 0;
        capacity = 0;

        scanIter = scan();
    }

    public static Table create(IOHelper io, String name, int schemaBlock) {
        try {
            Table ret = new Table(io, name, schemaBlock);
            ret.front = Cell.create(io, ret.getSchema()).save();
            ret.rear = ret.front;
            ret.capacity = (io.getBlockSize() - 16) / (4 + ret.schema.estimatedTupleSize());
            if (ret.capacity == 0)
                ret.capacity = 1;

            ret.head = Bucket.create(io, ret.getHeadBytes());
            return ret;
        } catch (java.io.IOException e) {
            return null;
        }
    }

    private byte[] getHeadBytes() {
        byte[] data = new byte[12];
        ByteLib.intToBytes(front, data, 0);
        ByteLib.intToBytes(rear, data, 4);
        ByteLib.intToBytes(capacity, data, 8);
        return data;
    }

    public static Table load(IOHelper io, int block, String name, int schema) {
        Table ret = new Table(io, name, schema);
        ret.head = Bucket.load(io, block);
        byte[] data = ret.head.getData();
        ret.front = ByteLib.bytesToInt(data, 0);
        ret.rear = ByteLib.bytesToInt(data, 4);
        ret.capacity = ByteLib.bytesToInt(data, 8);
        return ret;
    }

    public int save() throws java.io.IOException {
        head.setData(getHeadBytes());
        return head.save();
    }

    void remove() {
        schema.remove();
        int next = front;
        do {
            Cell cell = Cell.load(io, getSchema(), next);
            cell.remove();
            next = cell.getNext();
        } while (next != 0);
        head.remove();
    }

    public boolean insert(Map<String, DataEntity> map) {
        try {
            Tuple tuple = Tuple.create(getSchema(), map);
            if (tuple == null)
                return false;
            insert(tuple);
            return true;
        } catch (java.io.IOException e) {
            return false;
        }
    }

    public boolean insert(DataEntity[] tuple) {
        try {
            Schema schema = getSchema();
            Tuple t = Tuple.create(schema, tuple);
            if (t == null)
                return false;
            else {
                insert(t);
                return true;
            }
        } catch (java.io.IOException e) {
            return false;
        }
    }

    private void insert(Tuple tuple) throws java.io.IOException {
        Cell cell = Cell.load(io, getSchema(), rear);
        cell.insert(tuple);
        cell.save();
        if (cell.tupleCount() >= capacity) {
            rear = Cell.create(io, getSchema()).save();
            cell.setNext(rear);
            cell.save();
            save();
        }
    }

    public boolean update(Map<String, DataEntity> map) {
        return scanIter.update(map);
    }

    public Schema getSchema() {
        return schema.schema();
    }

    public void beforeFirst() {
        scanIter.beforeFirst();
    }

    public boolean next() {
        return scanIter.next();
    }

    public void delete() {
        scanIter.remove();
    }

    public boolean hasField(String name) {
        return getSchema().hasField(name);
    }

    public DataEntity getFieldByIndex(int index) {
        return scanIter.getField(index);
    }

    public DataEntity getField(String name) {
        return scanIter.getField(name);
    }

    public DataEntity[] tuple() {
        return scanIter.getTuple();
    }

    private class ScanIterator implements Iterator {
        private Cell currentCell = null;
        private int currentIndex = 0;
        private boolean removed = false;

        public void beforeFirst() {
            currentCell = Cell.load(io, getSchema(), front);
            currentIndex = -1;
            removed = false;
        }
        
        public boolean next() {
            int t = currentIndex + 1;
            if (t >= currentCell.tupleCount()) {
                do {
                    int nextCell = currentCell.getNext();
                    if (nextCell == 0)
                        return false;
                    currentCell = Cell.load(io, getSchema(), nextCell);
                } while (currentCell.tupleCount() == 0);

                currentIndex = 0;
                removed = false;
                return true;
            } else {
                currentIndex = t;
                removed = false;
                return true;
            }
        }

        public void remove() {
            try {
                if (!removed && currentCell != null && currentIndex >= 0 && currentIndex < currentCell.tupleCount()) {
                    currentCell.remove(currentIndex);
                    currentCell.save();
                    removed = true;
                    --currentIndex;
                }
            } catch (java.io.IOException e) {
            }
        }

        public boolean update(Map<String, DataEntity> map) {
            try {
                if (!removed && currentCell != null && currentIndex >= 0 && currentIndex < currentCell.tupleCount()) {
                    Tuple tuple = Tuple.create(getSchema(), map, getTuple());
                    if (tuple == null)
                        return false;
                    currentCell.set(currentIndex, tuple);
                    currentCell.save();
                    return true;
                } else
                    return false;
            } catch (java.io.IOException e) {
                return false;
            }
        }

        public DataEntity[] getTuple() {
            if (!removed && currentCell != null && currentIndex >= 0 && currentIndex < currentCell.tupleCount())
                return currentCell.get(currentIndex).tuple();
            else
                return null;
        }

        public DataEntity getField(int index) {
            DataEntity[] tuple = getTuple();
            if (tuple == null)
                return null;
            else
                return tuple[index];
        }

        public DataEntity getField(String fldname) {
            Schema schema = getSchema();
            if (schema == null)
                return null;
            else {
                int i = schema.index(fldname);
                if (i == -1)
                    return null;
                else
                    return getField(i);
            }
        }
    }

    private class DummyIndexIterator extends ScanIterator {
        String fldname;
        DataEntity value;
        DataComparator compare;

        public DummyIndexIterator(String fldname, DataEntity value, DataComparator compare) {
            super();
            this.fldname = fldname;
            this.value = value;
            this.compare = compare;
        }

        public boolean next() {
            boolean ret = super.next();
            while (ret) {
                if (compare.compare(getField(fldname), value))
                    break;
                else
                    ret = super.next();
            }
            return ret;
        }
    }

    public Iterator scan() {
        return new ScanIterator();
    }

    public void createIndex(String col) {
        // TODO
    }

    public void dropIndex(String col) {
        // TODO
    }

    public Iterator indexEqual(String col, DataEntity value) {
        return new DummyIndexIterator(col, value, new EqualToComparator());
    }

    public Iterator indexLessThan(String col, DataEntity value) {
        return new DummyIndexIterator(col, value, new LessThanComparator());
    }

    public Iterator indexLessThanEqual(String col, DataEntity value) {
        return new DummyIndexIterator(col, value, new LessThanEqualToComparator());
    }

    public Iterator indexGreaterThan(String col, DataEntity value) {
        return new DummyIndexIterator(col, value, new GreaterThanComparator());
    }

    public Iterator indexGreaterThanEqual(String col, DataEntity value) {
        return new DummyIndexIterator(col, value, new GreaterThanEqualToComparator());
    }

    public DataEntity max(String col) {
        Iterator iter = scan();
        iter.beforeFirst();
        DataComparator compare = new GreaterThanComparator();
        DataEntity ret = null;
        while (iter.next()) {
            DataEntity value = iter.getField(col);
            if (ret == null && !value.isNull())
                ret = value;
            else if (compare.compare(value, ret))
                ret = value;
        }
        return ret;
    }

    public DataEntity min(String col) {
        Iterator iter = scan();
        iter.beforeFirst();
        DataComparator compare = new LessThanComparator();
        DataEntity ret = null;
        while (iter.next()) {
            DataEntity value = iter.getField(col);
            if (ret == null && !value.isNull())
                ret = value;
            else if (compare.compare(value, ret))
                ret = value;
        }
        return ret;
    }
}
