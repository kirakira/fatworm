package fatworm.storage;

import java.io.RandomAccessFile;

public class Database implements IOHelper {
    private File file;
    private FreeList freeList;

    public Database(String name) throws java.io.FileNotFoundException, java.io.IOException {
        load(name);
    }

    private void load(String name) throws java.io.FileNotFoundException, java.io.IOException {
        RandomAccessFile raf = new RandomAccessFile(name, "rw");
        file = new File(raf);

        freeList = FreeList.load(file);
        if (freeList == null) {
            freeList = new FreeList();
        }
    }

    public void close() throws java.io.IOException {
        freeList.save(file);
    }

    public boolean readBlock(int block, byte[] data, int offset) throws java.io.IOException {
        return file.readBlock(block, data, offset);
    }

    public void writeBlock(int block, byte[] data, int offset) throws java.io.IOException {
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
}
