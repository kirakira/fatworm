package fatworm.storage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import fatworm.util.ByteLib;

class FreeList {
    private byte[] data;
    private boolean dirty;

    private List<Integer> blocks;

    public static final int reservedBlock = 3;

    public FreeList() {
        blocks = new LinkedList<Integer>();

        data = new byte[File.blockSize];
        Arrays.fill(data, (byte) 0);

        dirty = true;

        setUse(0);
        setUse(1);
        blocks.add(new Integer(2));
        setUse(2);
        setUse(reservedBlock);
    }

    private int leaves() {
        return data.length << 2;
    }

    public int size() {
        return data.length;
    }

    public int sizeInBlocks() {
        return blocks.size();
    }

    private byte bitSet(byte x, int pos) {
        pos = 7 - pos;
        return (byte) (x | (1 << pos));
    }
    
    private byte bitReset(byte x, int pos) {
        pos = 7 - pos;
        return (byte) (x & ~(1 << pos));
    }

    private byte bitGet(byte x, int pos) {
        pos = 7 - pos;
        return (byte) ((x >> pos) & 1);
    }

    private void set(int i) {
        ++i;
        data[i >> 3] = bitSet(data[i >> 3], i & 7);
    }

    private void reset(int i) {
        ++i;
        data[i >> 3] = bitReset(data[i >> 3], i & 7);
    }

    private byte get(int i) {
        ++i;
        return bitGet(data[i >> 3], i & 7);
    }

    private void update(int p) {
        int l, r;

        do {
            p = ((p - 1) >> 1);
            l = (p << 1) + 1;
            r = l + 1;
            if (get(l) != 0 && get(r) != 0)
                set(p);
            else
                reset(p);
        } while (p > 0);
    }

    private void setUse(int i) {
        int p = i + leaves() - 1;
        set(p);
        update(p);
    }

    private void resetUse(int i) {
        int p = i + leaves() - 1;
        reset(p);
        update(p);
    }

    private int getFree() {
        int i = 0, l = leaves();
        while (i < l - 1) {
            if (get(i) != 0)
                return -1;

            i = (i << 1) + 1;
            if (get(i) != 0)
                i = i + 1;
        }

        return i - l + 1;
    }

    private void expand() {
        int newSize = size() * 2;
        int blocksToAdd = sizeInBlocks();
        byte[] newData = new byte[newSize];

        Arrays.fill(newData, (byte) 0);
        System.arraycopy(data, leaves() >> 3, newData, leaves() >> 2, leaves() >> 3);

        data = newData;

        for (int i = leaves() - 2; i >= 0; --i) {
            int l = i * 2 + 1, r = l + 1;
            if (get(l) != 0 && get(r) != 0)
                set(i);
            else
                reset(i);
        }

        for (int i = 0; i < blocksToAdd; ++i)
            blocks.add(new Integer(occupy()));
    }

    private boolean check() {
        for (int i = 0; i < leaves() - 1; ++i) {
            int l = i * 2 + 1, r = l + 1;
            if (get(l) != 0 && get(r) != 0 && get(i) == 0)
                return false;
            if ((get(l) ==0 || get(r) == 0) && get(i) != 0)
                return false;
        }
        return true;
    }

    public int occupy() {
        int r = getFree();
        if (r != -1) {
            dirty = true;
            setUse(r);
            return r;
        } else {
            expand();
            return occupy();
        }
    }

    public void free(int i) {
        if (i < leaves() && get(i + leaves() - 1) != 0) {
            resetUse(i);
            dirty = true;
        } 
    }

    public void save(File file) throws java.io.IOException {
        if (!dirty)
            return;

        byte[] tmp = new byte[2 * File.blockSize];
        ByteLib.intToBytes(blocks.size(), tmp, 0);
        int c = 0;
        for (Integer i: blocks) {
            ++c;
            ByteLib.intToBytes(i.intValue(), tmp, c * 4);
        }
        file.writeBlock(0, 2, tmp, 0);

        c = 0;
        for (Integer i: blocks) {
            file.writeBlock(i.intValue(), data, c * File.blockSize);
            ++c;
        }

        dirty = false;
    }

    public static FreeList load(File file) {
        try {
            FreeList ret = new FreeList();

            byte[] tmp = new byte[2 * File.blockSize];
            if (!file.readBlock(0, 2, tmp, 0))
                return null;
            int c = ByteLib.bytesToInt(tmp, 0);

            ret.blocks = new LinkedList<Integer>();
            for (int i = 0; i < c; ++i)
                ret.blocks.add(new Integer(ByteLib.bytesToInt(tmp, (i + 1) * 4)));

            ret.data = new byte[ret.blocks.size() * File.blockSize];
            c = 0;
            for (Integer i: ret.blocks) {
                if (!file.readBlock(i.intValue(), ret.data, c * File.blockSize))
                    return null;
                ++c;
            }

            ret.dirty = false;

            return ret;
        } catch (Exception e) {
            return null;
        }
    }
}
