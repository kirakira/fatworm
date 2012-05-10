package fatworm.storage.bplustree;

import fatworm.util.ByteLib;
import java.util.Comparator;

public class IntegerComparator implements Comparator<byte[]> {
    public int compare(byte[] o1, byte[] o2) {
        int r = ByteLib.bytesToInt(o1, 0) - ByteLib.bytesToInt(o2, 0);
        if (r < 0)
            return -1;
        else if (r == 0)
            return 0;
        else
            return 1;
    }

    public boolean equals(Object o) {
        return (o instanceof IntegerComparator);
    }
}
