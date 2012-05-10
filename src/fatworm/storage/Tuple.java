package fatworm.storage;

import java.util.Map;
import fatworm.util.ByteLib;
import fatworm.dataentity.*;
import fatworm.record.Schema;

public class Tuple {
    byte[][] values;

    private Tuple() {
    }

    public static Tuple create(Map<String, DataEntity> map, Schema schema) {
        Tuple ret = new Tuple();

        int len = schema.columnCount();
        ret.values = new byte[len][];

        if (len != map.size())
            return null;

        int count = 0;
        for (int i = 0; i < len; ++i) {
            DataEntity de = map.get(schema.name(i));
            if (de == null)
                return null;
            ++count;
            ret.values[i] = de.getBytes();
        }

        return ret;
    }

    public Tuple(byte[] data, int offset, int dlen) {
        int s = offset;
        int count = ByteLib.bytesToInt(data, s);
        s += 4;

        values = new byte[count][];

        for (int i = 0; i < count; ++i) {
            int len = ByteLib.bytesToInt(data, s);
            s += 4;
            values[i] = new byte[len];

            System.arraycopy(data, s, values[i], 0, len);
            s += len;
        }
    }

    public byte[] getBytes() {
        int len = 4;
        for (int i = 0; i < values.length; ++i)
            len += 4 + values[i].length;

        byte[] data = new byte[len];
        int s = 0;
        ByteLib.intToBytes(values.length, data, s);
        s += 4;
        for (int i = 0; i < values.length; ++i) {
            ByteLib.intToBytes(values[i].length, data, s);
            s += 4;

            System.arraycopy(values[i], 0, data, s, values[i].length);
            s += values[i].length;
        }

        return data;
    }
}
