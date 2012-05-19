package fatworm.storage;

import fatworm.util.ByteBuffer;
import fatworm.dataentity.*;

import static java.sql.Types.*;
import java.util.Comparator;

public class DataAdapter {
    int type;
    int length;

    public DataAdapter(int type, int length) {
        this.type = type;
        this.length = length;
    }

    public byte[] putData(DataEntity de) {
        ByteBuffer buffer = new ByteBuffer();
        de.getBytes(buffer);
        return buffer.array();
    }

    public DataEntity getData(byte[] data) {
        ByteBuffer buffer = new ByteBuffer(data);
        return DataEntity.fromBytes(type, buffer);
    }

    public Comparator<byte[]> comparator() {
        return new Comparator<byte[]>() {
            public int compare(byte[] a, byte[] b) {
                DataEntity da = getData(a), db = getData(b);
                return da.compareTo(db);
            }
        };
    }

    public int averageKeySize() {
        if (type == CHAR) {
            return length + 4;
        } else if (type == DECIMAL) {
            return length + 6;
        } else if (type == VARCHAR) {
            return length / 3 + 4;
        } else
            return length;
    }

    public boolean isVariant() {
        return (type == DECIMAL || type == CHAR || type == VARCHAR);
    }
}
