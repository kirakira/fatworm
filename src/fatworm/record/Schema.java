package fatworm.record;

import static java.sql.Types.*;
import java.util.*;
import fatworm.util.ByteLib;

/**
 * The record schema of a table.
 * A schema contains the name and type of
 * each field of the table, as well as the length
 * of each varchar field.
 * @author Edward Sciore
 *
 */
public class Schema {
    private ArrayList<FieldInfo> info = new ArrayList<FieldInfo>();

    /**
     * Creates an empty schema.
     * Field information can be added to a schema
     * via the five addXXX methods. 
     */
    public Schema() {}

    /**
     * Adds a field to the schema having a specified
     * name, type, and length.
     * If the field type is "integer", then the length
     * value is irrelevant.
     * @param fldname the name of the field
     * @param type the type of the field, according to the constants in simpledb.sql.types
     * @param length the conceptual length of a string field.
     */
    public boolean addField(String fldname, int type, int length) {
        if (indexOf(fldname) != -1)
            return false;
        info.add(new FieldInfo(fldname, type, length));
    }

    /**
     * Returns true if the specified field
     * is in the schema
     * @param fldname the name of the field
     * @return true if the field is in the schema
     */
    public boolean hasField(String fldname) {
        if (indexOf(fldname) == -1)
            return false;
        else
            return true;
    }

    private int indexOf(String fldname) {
        for (int i = 0; i < info.size(); ++i)
            if (info.get(i).name.equals(fldname))
                return i;
        return -1;
    }

    /**
     * Returns the type of the specified field, using the
     * constants in {@link java.sql.Types}.
     * @param fldname the name of the field
     * @return the integer type of the field
     */
    public int type(String fldname) {
        return info.get(indexOf(fldname)).type;
    }

    /**
     * Returns the conceptual length of the specified field.
     * If the field is not a string field, then
     * the return value is undefined.
     * @param fldname the name of the field
     * @return the conceptual length of the field
     */
    public int length(String fldname) {
        return info.get(indexOf(fldname)).length;
    }

    public byte[] getBytes() {
        int len = 4;
        for (int i = 0; i < info.size(); ++i) {
            len += 4;
            len += ByteLib.stringToBytes(info.get(i).name).length;
            len += 8;
        }

        byte[] data = new byte[len];
        int s = 0;
        ByteLib.intToBytes(info.size(), data, s);
        s += 4;
        for (int i = 0; i < info.size(); ++i) {
            FieldInfo field = info.get(i);
            byte[] tmp = ByteLib.stringToBytes(field.name);
            ByteLib.intToBytes(tmp.length, data, s);
            s += 4;
            System.arraycopy(tmp, 0, data, s, tmp.length);
            s += tmp.length;

            ByteLib.intToBytes(field.type, data, s);
            s += 4;
            ByteLib.intToBytes(field.length, data, s);
            s += 4;
        }

        return data;
    }

    public Schema(byte[] data, int offset, int len) {
        int s = offset;
        int count = ByteLib.bytesToInt(data, s);
        s += 4;
        for (int i = 0; i < count; ++i) {
            int slen = ByteLib.bytesToInt(data, s);
            s += 4;
            String name = ByteLib.bytesToString(data, s, slen);
            s += slen;
            int type = ByteLib.bytesToInt(data, s);
            s += 4;
            int length = ByteLib.bytesToInt(data, s);
            s += 4;

            addField(name, type, length);
        }
    }

    class FieldInfo {
        String name;
        int type, length;
        public FieldInfo(String name, int type, int length) {
            this.name = name;
            this.type = type;
            this.length = length;
        }
    }

    public int estimatedLength() {
        int tot = 0;
        for (int i = 0; i < info.size(); ++i) {
            FieldInfo field = info.get(i);
            if (field.type == VARCHAR)
                tot += field.length / 3;
            else if (field.type == DECIMAL)
                tot += field.length + 2;
            else
                tot += field.length;
        }
        return tot;
    }
}
