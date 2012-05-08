package fatworm.storage;

import java.io.RandomAccessFile;

public class File {
    private RandomAccessFile file;
    static int blockSize = 4 * 1024;

    public File(RandomAccessFile file) {
        this.file = file;
    }

    public boolean readBlock(int block, int count, byte[] data, int offset) throws java.io.IOException {
        long t0 = System.nanoTime();
        file.seek((long) block * (long) blockSize);
        boolean ret = (file.read(data, offset, count * blockSize) == count * blockSize);
        totalTime += System.nanoTime() - t0;
        return ret;
    }

    public void writeBlock(int block, int count, byte[] data, int offset) throws java.io.IOException {
        long t0 = System.nanoTime();
        file.seek((long) block * (long) blockSize);
        file.write(data, offset, count * blockSize);
        totalTime += System.nanoTime() - t0;
    }

    public boolean readBlock(int block, byte[] data, int offset) throws java.io.IOException {
        return readBlock(block, 1, data, offset);
    }

    public void writeBlock(int block, byte[] data, int offset) throws java.io.IOException {
        writeBlock(block, 1, data, offset);
    }

    public static long totalTime = 0;
}
