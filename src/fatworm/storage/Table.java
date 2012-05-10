package fatworm.storage;

import fatworm.record.TableInfo;
import fatworm.storage.bucket.Bucket;

public class Table implements TableInfo {
    private IOHelper io;
    private String name;
    private Schema schema;

    private Bucket head;

    private int front, rear;
    private int capacity;
    
    private Table(IOHelper io, String name, int schema) {
        this.io = io;
        this.name = name;
        this.schema = Schema.load(io, schema);
        front = 0;
        rear = 0;
        capacity = 0;
    }

    public static Table create(IOHelper io, String name, int schema) {
        Table ret = new Table(io, name, schema);
        ret.front = Cell.create(io).save();
        ret.rear = ret.front;
        ret.capacity = (io.getBlockSize() - 8) / schema.estimatedTupleSize();
        if (ret.capacity == 0)
            ret.capacity = 1;

        ret.head = Bucket.create(io, ret.getHeadBytes());
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
    }

    public int save() {
        head.setData(getHeadBytes());
        return head.save();
    }

    public void insert(Tuple tuple) {
        Cell cell = Cell.load(io, rear);
        cell.insert(tuple);
        cell.save();
        if (cell.tupleCount() >= capacity) {
            rear = Cell.create(io).save();
            save();
        }
    }
}
