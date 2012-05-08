package fatworm.storage.bplustree;

import java.util.List;
import java.util.LinkedList;
import fatworm.storage.*;
import java.util.Iterator;

class Bucket {
    private IOHelper io;
    private static int fanout = 1022;

    public Bucket(IOHelper io) {
        this.io = io;
    }

    public List<Integer> load(int block) {
        if (block == 0)
            return null;

        try {
            List<Integer> ret = new LinkedList<Integer>();

            byte[] data = new byte[io.getBlockSize()];
            int current = block;

            while (current != 0) {
                io.readBlock(current, data, 0);
                int c = ByteLib.bytesToInt(data, 4);
                for (int i = 0; i < c; ++i)
                    ret.add(new Integer(ByteLib.bytesToInt(data, (2 + i) * 4)));
                current = ByteLib.bytesToInt(data, 0);
            }

            return ret;
        } catch (java.io.IOException e) {
            return null;
        }
    }

    public int create(List<Integer> list) throws java.io.IOException {
        byte[] data = new byte[io.getBlockSize()];

        int ret = io.occupy(), current = ret;
        int c = 0;
        for (Iterator<Integer> iter = list.iterator(); ; ) {
            int value = iter.next().intValue();
            ByteLib.intToBytes(value, data, (2 + c) * 4);

            ++c;
            if (c == fanout || !iter.hasNext()) {
                ByteLib.intToBytes(c, data, 4);
                int next = 0;
                if (iter.hasNext())
                    next = io.occupy();
                ByteLib.intToBytes(next, data, 0);
                io.writeBlock(current, data, 0);

                if (iter.hasNext()) {
                    c = 0;
                    current = next;
                } else
                    break;
            }
        }

        return ret;
    }

    public void remove(int block) throws java.io.IOException {
        if (block == 0)
            return;

        byte[] data = new byte[io.getBlockSize()];

        io.readBlock(block, data, 0);
        int next = ByteLib.bytesToInt(data, 0);
        remove(next);
        io.free(block);
    }
}
