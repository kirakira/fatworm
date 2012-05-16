package fatworm.storage;

import java.io.RandomAccessFile;
import fatworm.storage.bplustree.*;
import fatworm.storage.bucket.Bucket;
import fatworm.util.ByteLib;
import fatworm.record.Schema;
import fatworm.storagemanager.*;

import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.Random;

public class Database implements IOHelper {
    private File file;
    private FreeList freeList;
    private SuperTable superTable;

    long readCount = 0, writeCount = 0;

    public Database(String name) throws java.io.FileNotFoundException, java.io.IOException {
        load(name);
    }

    private void load(String name) throws java.io.FileNotFoundException, java.io.IOException {
        RandomAccessFile raf = new RandomAccessFile(name, "rw");
        file = new File(raf);

        freeList = FreeList.load(file);
        if (freeList == null) {
            freeList = new FreeList();
            superTable = SuperTable.create(this);
        } else
            superTable = SuperTable.load(this);
    }

    public void save() throws java.io.IOException {
        superTable.save();
        freeList.save(file);
    }

    public boolean readBlock(int block, byte[] data, int offset) throws java.io.IOException {
        ++readCount;
        return file.readBlock(block, data, offset);
    }

    public void writeBlock(int block, byte[] data, int offset) throws java.io.IOException {
        ++writeCount;
        file.writeBlock(block, data, offset);
    }

    public int occupy() {
        return freeList.occupy();
    }

    public void free(int block) {
        freeList.free(block);
    }

    public int getBlockSize() {
        return File.blockSize;
    }

    public Table getTable(String table) throws java.io.IOException {
        int relation = superTable.getRelation(table), schema = superTable.getSchema(table);
        if (relation == 0)
            return null;
        else
            return Table.load(this, relation, schema);
    }

    // returns null if the table name already existed
    public Table insertTable(String table, Schema schema) throws java.io.IOException {
        if (getTable(table) != null)
            return null;
        int sBlock;
        if (schema == null)
            sBlock = 0;
        else {
            SchemaOnDisk ss = SchemaOnDisk.create(this, schema);
            sBlock = ss.save();
        }
        Table st = Table.create(this, sBlock);
        int tBlock = st.save();
        superTable.insertTable(table, tBlock, sBlock);
        return st;
    }

    Table insertTable(String table, int tupleSize) throws java.io.IOException {
        if (getTable(table) != null)
            return null;
        int sBlock;
        Table st = Table.createTemp(this, tupleSize);
        int tBlock = st.save();
        superTable.insertTable(table, tBlock, 0);
        return st;
    }

    public void dropTable(String tablename) throws java.io.IOException {
        Table table = getTable(tablename);
        if (table != null) {
            table.remove();
            superTable.removeTable(tablename);
        }
    }

    public void close() {
    	file.close();
    }
}
