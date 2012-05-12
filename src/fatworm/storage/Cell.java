package fatworm.storage;

import fatworm.storage.bucket.Bucket;
import fatworm.util.ByteLib;
import fatworm.record.Schema;

import java.util.ArrayList;

public class Cell {
    private IOHelper io;
    private Schema schema;
    private Bucket bucket;
    private ArrayList<Tuple> tuples;
    private int next;

    private Cell(IOHelper io, Schema schema) {
        this.io = io;
        this.schema = schema;
        bucket = null;
        tuples = new ArrayList<Tuple>();
        next = 0;
    }

    public static Cell create(IOHelper io, Schema schema) {
        Cell ret = new Cell(io, schema);

        ret.bucket = Bucket.create(io, null);
        return ret;
    }

    public static Cell load(IOHelper io, Schema schema, int block) {
        Cell ret = new Cell(io, schema);
        ret.bucket = Bucket.load(io, block);

        byte[] data = ret.bucket.getData();
        int s = 0;
        ret.next = ByteLib.bytesToInt(data, s);
        s += 4;
        int len = ByteLib.bytesToInt(data, s);
        s += 4;
        for (int i = 0; i < len; ++i) {
            int tlen = ByteLib.bytesToInt(data, s);
            s += 4;

            ret.tuples.add(new Tuple(ret.schema, data, s));
            s += tlen;
        }

        return ret;
    }

    private byte[] getBytes() {
        int len = 8;
        byte[][] buffer = new byte[tuples.size()][];
        for (int i = 0; i < tuples.size(); ++i) {
            buffer[i] = tuples.get(i).getBytes();
            len += 4 + buffer[i].length;
        }

        byte[] data = new byte[len];
        int s = 0;
        ByteLib.intToBytes(next, data, s);
        s += 4;
        ByteLib.intToBytes(tuples.size(), data, s);
        s += 4;
        for (int i = 0; i < tuples.size(); ++i) {
            ByteLib.intToBytes(buffer[i].length, data, s);
            s += 4;

            System.arraycopy(buffer[i], 0, data, s, buffer[i].length);
            s += buffer[i].length;
        }

        return data;
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

    public int save() throws java.io.IOException {
        bucket.setData(getBytes());
        int ret = bucket.save();
        if (bucket.blockCount() > 1)
            System.out.println("Cell saved in more than 1 block");
        return ret;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }
}
