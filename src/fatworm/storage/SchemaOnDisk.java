package fatworm.storage;

import fatworm.storage.bucket.Bucket;
import fatworm.record.Schema;
import fatworm.util.ByteBuffer;

public class SchemaOnDisk {
    private Bucket bucket;
    private Schema schema;

    private SchemaOnDisk() {
        schema = null;
        bucket = null;
    }

    public static SchemaOnDisk load(IOHelper io, int block) {
        SchemaOnDisk ret = new SchemaOnDisk();
        ret.bucket = Bucket.load(io, block);
        if (ret.bucket == null)
            return null;
        byte[] data = ret.bucket.getData();
        ByteBuffer buffer = new ByteBuffer(data);
        ret.schema = new Schema(buffer);
        return ret;
    }

    public static SchemaOnDisk create(IOHelper io, fatworm.record.Schema schema) {
        SchemaOnDisk ret = new SchemaOnDisk();
        ret.schema = schema;
        ret.bucket = Bucket.create(io, null);
        return ret;
    }

    public void remove() {
        bucket.remove();
    }

    public int save() throws java.io.IOException {
        ByteBuffer buffer = new ByteBuffer();
        schema.getBytes(buffer);
        bucket.setData(buffer.array());
        return bucket.save();
    }

    public Schema schema() {
        return schema;
    }

    public int estimatedTupleSize() {
        return schema.estimatedLength() + 4 * schema.columnCount() + 4;
    }
}
