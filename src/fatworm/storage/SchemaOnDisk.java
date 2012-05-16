package fatworm.storage;

import fatworm.storage.bucket.Bucket;
import fatworm.record.Schema;
import fatworm.util.ByteBuffer;

public class SchemaOnDisk {
    private Bucket bucket;
    private Schema schema;
    private int[] index;

    private SchemaOnDisk() {
        schema = null;
        bucket = null;
        index = null;
    }

    public static SchemaOnDisk load(IOHelper io, int block) {
        SchemaOnDisk ret = new SchemaOnDisk();
        ret.bucket = Bucket.load(io, block);
        if (ret.bucket == null)
            return null;
        byte[] data = ret.bucket.getData();
        ByteBuffer buffer = new ByteBuffer(data);
        ret.schema = new Schema(buffer);
        int len = ret.schema.columnCount();
        ret.index = new int[len];
        for (int i = 0; i < len; ++i)
            ret.index[i] = buffer.getInt();
        return ret;
    }

    public static SchemaOnDisk create(IOHelper io, fatworm.record.Schema schema) {
        SchemaOnDisk ret = new SchemaOnDisk();
        ret.schema = schema;
        ret.bucket = Bucket.create(io);
        int len = schema.columnCount();
        ret.index = new int[len];
        for (int i = 0; i < len; ++i)
            ret.index[i] = 0;
        return ret;
    }

    public void remove() {
        bucket.remove();
    }

    public int save() throws java.io.IOException {
        ByteBuffer buffer = new ByteBuffer();
        schema.getBytes(buffer);
        int len = schema.columnCount();
        for (int i = 0; i < len; ++i)
            buffer.putInt(index[i]);
        bucket.setData(buffer.array());
        return bucket.save();
    }

    public Schema schema() {
        return schema;
    }

    public int estimatedTupleSize() {
        return schema.estimatedLength() + 4 * schema.columnCount() + 4;
    }

    public boolean hasIndex(int column) {
        return (index(column) != 0);
    }

    public int index(int column) {
        return index[column];
    }

    public void index(int column, int block) {
        index[column] = block;
    }
}
