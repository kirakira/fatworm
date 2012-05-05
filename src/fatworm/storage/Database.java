package fatworm.storage;

import java.io.RandomAccessFile;

public class Database {
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
}
