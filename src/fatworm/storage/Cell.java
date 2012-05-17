package fatworm.storage;

import fatworm.storage.bucket.Bucket;
import fatworm.util.ByteBuffer;
import fatworm.record.Schema;

import java.util.ArrayList;

public class Cell {
    private IOHelper io;
    private Bucket bucket;
    private ArrayList<Tuple> tuples;
    private int next;

    private Cell(IOHelper io) {
        this.io = io;
        bucket = null;
        tuples = new ArrayList<Tuple>();
        next = 0;
    }

    public static Cell create(IOHelper io) {
        Cell ret = new Cell(io);

        ret.bucket = Bucket.create(io, null);
        return ret;
    }

    public static Cell load(IOHelper io, int block) {
        Cell ret = new Cell(io);
        ret.bucket = Bucket.load(io, block);

        byte[] data = ret.bucket.getData();
        ByteBuffer buffer = new ByteBuffer(data);

        ret.next = buffer.getInt();
        int len = buffer.getInt();
        for (int i = 0; i < len; ++i)
            ret.tuples.add(new Tuple(buffer));

        return ret;
    }

    private void getBytes(ByteBuffer buffer) {
        buffer.putInt(next);
        buffer.putInt(tuples.size());
        for (Tuple t: tuples)
            t.getBytes(buffer);
    }

    public int save() throws java.io.IOException {
        ByteBuffer buffer = new ByteBuffer();
        getBytes(buffer);
        bucket.setData(buffer.array());
        return bucket.save();
    }

    public void remove() {
        bucket.remove();
    }

    public Tuple get(int index) {
        return tuples.get(index);
    }

    public void set(int index, Tuple t) {
        tuples.set(index, t);
    }

    public int tupleCount() {
        return tuples.size();
    }

    public void insert(Tuple tuple) {
        tuples.add(tuple);
    }

    public void remove(int index) {
        tuples.remove(index);
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }
}
