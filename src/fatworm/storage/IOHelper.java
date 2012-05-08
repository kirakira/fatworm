package fatworm.storage;

public interface IOHelper {
    public int getBlockSize();

    public boolean readBlock(int block, byte[] data, int offset) throws java.io.IOException;
    public void writeBlock(int block, byte[] data, int offset) throws java.io.IOException;

    public int occupy();
    public void free(int block);
}
