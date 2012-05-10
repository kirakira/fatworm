package fatworm.storage;

import fatworm.storage.bucket.Bucket;
import fatworm.util.ByteLib;

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
        int s = 0;
        ret.next = ByteLib.bytesToInt(data, s);
        s += 4;
        int len = ByteLib.bytesToInt(data, s);
        s += 4;
        for (int i = 0; i < len; ++i) {
            int tlen = ByteLib.bytesToInt(data, s);
            s += 4;

            ret.tuples.add(new Tuple(data, s));
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

    public Tuple get(int index) {
        return tuples.get(index);
    }

    public Tuple set(int index, Tuple t) {
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

    public int save() {
        bucket.setData(getBytes());
        return bucket.save();
    }

    public int next() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }
}
