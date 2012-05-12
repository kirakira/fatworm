package fatworm.storage.bucket;

import java.util.LinkedList;
import fatworm.storage.*;
import fatworm.util.ByteLib;

public class Bucket {
    private int block;
    private IOHelper io;
    private byte[] data;

    private LinkedList<Integer> blocks;

    private Bucket() {
    }

    public int blockCount() {
        return 1 + blocks.size();
    }

    public static Bucket load(IOHelper io, int block) {
        try {
            Bucket bucket = new Bucket();
            bucket.block = block;
            bucket.io = io;
            bucket.blocks = new LinkedList<Integer>();

            byte[] tmp = new byte[io.getBlockSize()];
            if (!io.readBlock(block, tmp, 0))
                return null;

            int next = ByteLib.bytesToInt(tmp, 0);
            int len = ByteLib.bytesToInt(tmp, 4);
            bucket.data = new byte[len];

            if (len < io.getBlockSize() - 8)
                System.arraycopy(tmp, 8, bucket.data, 0, len);
            else {
                int s = io.getBlockSize() - 8;
                System.arraycopy(tmp, 8, bucket.data, 0, s);
                while (next != 0) {
                    if (!io.readBlock(next, tmp, 0))
                        return null;

                    bucket.blocks.add(new Integer(next));

                    int r;
                    if (len - s > io.getBlockSize() - 4)
                        r = io.getBlockSize() - 4;
                    else
                        r = len - s;
                    System.arraycopy(tmp, 4, bucket.data, s, r);
                    s += r;

                    next = ByteLib.bytesToInt(tmp, 0);
                }
            }

            return bucket;
        } catch (java.io.IOException e) {
            return null;
        }
    }

    public static Bucket create(IOHelper io, byte[] data, int block) {
        Bucket ret = Bucket.create(io, data);
        ret.block = block;
        return ret;
    }

    public static Bucket create(IOHelper io, byte[] data) {
        Bucket bucket = new Bucket();
        bucket.block = 0;
        bucket.io = io;
        bucket.blocks = new LinkedList<Integer>();
        bucket.setData(data);
        return bucket;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void remove() {
        if (block != 0)
            io.free(block);
        for (Integer i: blocks)
            io.free(i.intValue());
        blocks = new LinkedList<Integer>();
        block = 0;
    }

    public int save() throws java.io.IOException {
        byte[] tmp = new byte[io.getBlockSize()];
        
        if (block == 0)
            block = io.occupy();

        LinkedList<Integer> oldBlocks = blocks;
        blocks = new LinkedList<Integer>();

 
        ByteLib.intToBytes(data.length, tmp, 4);

        if (data.length <= io.getBlockSize() - 8) {
            ByteLib.intToBytes(0, tmp, 0);
            System.arraycopy(data, 0, tmp, 8, data.length);
            io.writeBlock(block, tmp, 0);
        } else {
            int next, r = io.getBlockSize() - 8;

            if (!oldBlocks.isEmpty())
                next = oldBlocks.removeFirst();
            else
                next = io.occupy();
            ByteLib.intToBytes(next, tmp, 0);
            System.arraycopy(data, 0, tmp, 8, r);
            io.writeBlock(block, tmp, 0);

            while (r < data.length) {
                int current = next;
                if (data.length - r <= io.getBlockSize() - 4) {
                    next = 0;
                    System.arraycopy(data, r, tmp, 4, data.length - r);
                    r = data.length;
                } else {
                    if (!oldBlocks.isEmpty())
                        next = oldBlocks.removeFirst();
                    else
                        next = io.occupy();
                    System.arraycopy(data, r, tmp, 4, io.getBlockSize() - 4);
                    r += io.getBlockSize() - 4;
                }
                ByteLib.intToBytes(next, tmp, 0);
                io.writeBlock(current, tmp, 0);
                blocks.add(new Integer(current));
            }
        }

        for (Integer i: oldBlocks)
            io.free(i.intValue());

        return block;
    }
}
