package fatworm.storage;

import java.util.Map;
import java.util.HashMap;
import fatworm.storage.bucket.Bucket;
import fatworm.util.ByteBuffer;

public class SuperTable {
    private static class MetaInfo {
        String name;
        int block;
        int schema;

        public MetaInfo(String name, int block, int schema) {
            this.name = name;
            this.block = block;
            this.schema = schema;
        }

        public MetaInfo(ByteBuffer buffer) {
            name = buffer.getString();
            block = buffer.getInt();
            schema = buffer.getInt();
        }

        public void getBytes(ByteBuffer buffer) {
            buffer.putString(name);
            buffer.putInt(block);
            buffer.putInt(schema);
        }
    }

    private Map<String, MetaInfo> tables = new HashMap<String, MetaInfo>();
    private Bucket bucket;
    private IOHelper io;

    public void insertTable(String name, int block, int schema) {
        tables.put(name, new MetaInfo(name, block, schema));
    }

    public void removeTable(String name) {
        tables.remove(name);
    }

    private SuperTable(IOHelper io) {
        this.io = io;
    }

    public static SuperTable create(IOHelper io) {
        SuperTable ret = new SuperTable(io);
        ret.bucket = Bucket.create(io, FreeList.reservedBlock);
        return ret;
    }

    public static SuperTable load(IOHelper io) {
        SuperTable ret = new SuperTable(io);
        ret.bucket = Bucket.load(io, FreeList.reservedBlock);
        byte[] data = ret.bucket.getData();
        ByteBuffer buffer = new ByteBuffer(data);

        int len = buffer.getInt();
        for (int i = 0; i < len; ++i) {
            MetaInfo mi = new MetaInfo(buffer);
            ret.tables.put(mi.name, mi);
        }

        return ret;
    }

    public int getRelation(String table) {
        MetaInfo mi = tables.get(table);
        if (mi == null)
            return 0;
        else
            return mi.block;
    }

    public int getSchema(String table) {
        MetaInfo mi = tables.get(table);
        if (mi == null)
            return 0;
        else
            return mi.schema;
    }

    public int save() throws java.io.IOException {
        ByteBuffer buffer = new ByteBuffer();
        buffer.putInt(tables.size());
        for (MetaInfo mi: tables.values())
            mi.getBytes(buffer);

        bucket.setData(buffer.array());
        return bucket.save();
    }
}
