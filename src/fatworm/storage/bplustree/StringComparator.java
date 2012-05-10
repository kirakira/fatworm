package fatworm.storage.bplustree;

import fatworm.util.ByteLib;
import java.util.Comparator;

public class StringComparator implements Comparator<byte[]> {
    public int compare(byte[] o1, byte[] o2) {
        return ByteLib.bytesToString(o1, 0, o1.length).compareTo(ByteLib.bytesToString(o2, 0, o2.length));
    }

    public boolean equals(Object o) {
        return (o instanceof StringComparator);
    }
}
