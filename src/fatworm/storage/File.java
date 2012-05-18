package fatworm.storage;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;

public class File {
    private RandomAccessFile file;
    private ByteBuffer buffer = null;
    static int blockSize = 4 * 1024;
    private int bufferSizeInBlock = 0;//125 * 1024;

    public File(RandomAccessFile file) {
        this.file = file;

        try {
            FileChannel channel = file.getChannel();
            buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, bufferSizeInBlock * blockSize);
        } catch (IOException e) {
            bufferSizeInBlock = 0;
            buffer = null;
        }
    }

    public boolean readBlock(int block, int count, byte[] data, int offset) throws java.io.IOException {
        if (block >= bufferSizeInBlock) {
            file.seek((long) block * (long) blockSize);
            boolean ret = (file.read(data, offset, count * blockSize) == count * blockSize);
            return ret;
        } else if (block + count <= bufferSizeInBlock) {
            buffer.position(block * blockSize);
            buffer.get(data, offset, blockSize * count);
            return true;
        } else {
            int c1 = block + count - bufferSizeInBlock;
            if (!readBlock(block, c1, data, offset))
                return false;
            if (!readBlock(block + c1, count - c1, data, offset + c1 * blockSize))
                return false;
            return true;
        }
    }

    public void writeBlock(int block, int count, byte[] data, int offset) throws java.io.IOException {
        if (block >= bufferSizeInBlock) {
            file.seek((long) block * (long) blockSize);
            file.write(data, offset, count * blockSize);
        } else if (block + count <= bufferSizeInBlock) {
            buffer.position(block * blockSize);
            buffer.put(data, offset, count * blockSize);
        } else {
            int c1 = block + count - bufferSizeInBlock;
            writeBlock(block, c1, data, offset);
            writeBlock(block + c1, count - c1, data, offset + c1 * blockSize);
        }
    }

    public boolean readBlock(int block, byte[] data, int offset) throws java.io.IOException {
        return readBlock(block, 1, data, offset);
    }

    public void writeBlock(int block, byte[] data, int offset) throws java.io.IOException {
        writeBlock(block, 1, data, offset);
    }
    
    public void close() {
    	try {
    		file.close();
    	} catch (java.io.IOException e) {
    	
    	}
    }
}
