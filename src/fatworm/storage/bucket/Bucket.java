package fatworm.storage.bucket;

import fatworm.storage.*;

public class Bucket {
    private int block;
    private IOHelper io;
    private byte[] data;

    private Bucket() {
    }

    public static Bucket load(IOHelper io, int block) {
        try {
            Bucket bucket = new Bucket();
            bucket.block = block;
            bucket.io = io;

            byte[] tmp = new byte[io.getBlockSize()];
            if (!io.readBlock(block, tmp, 0))
                return null;

            int next = ByteLib.bytesToInt(tmp, 0);
            int len = ByteLib.bytesToInt(tmp, 4);

            data = new byte[len];
            System.arraycopy(tmp, 8, data, 0, len);

            int s = io.getBlockSize();
            while (next != 0) {
                if (!io.readBlock(next, tmp, 0))
                    return null;

                if (len - s > io.getBlockSize() - 4)
                    r = io.getBlockSize - 4;
                else
                    r = len - s;
                System.arraycopy(tmp, 4, data, s, r);
                s += r;

                next = ByteLib.bytesToInt(tmp, 0);
            }

            return bucket;
        } catch (java.io.IOException) {
            return null;
        }
    }

    public static Bucket create(IOHelper io) {
    }

    public byte[] getData() {
    }

    public void setData(byte[] data) {
    }

    public void remove() {
    }

    public int save() {
    }
}
