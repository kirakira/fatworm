package fatworm.record;

import static java.sql.Types.*;
import java.util.*;

import fatworm.util.ByteBuffer;
import fatworm.dataentity.*;

public class Schema {
    private ArrayList<FieldInfo> info = new ArrayList<FieldInfo>();

    public Schema() {}

    public boolean addField(String fldname, int type, int length, boolean notNull, boolean autoIncrement, boolean primaryKey, DataEntity defaultValue) {
        if (indexOf(fldname) != -1)
            return false;
        if (primaryKey && hasPrimary())
            return false;
        if (autoIncrement && !primaryKey)
            return false;
        if (autoIncrement && type != INTEGER)
            return false;
        if (defaultValue == null || defaultValue.isNull())
            defaultValue = new NullDataEntity();
        else if (type != defaultValue.type())
            return false;
        info.add(new FieldInfo(fldname, type, length, notNull, autoIncrement, primaryKey, defaultValue));
        return true;
    }

    private boolean hasPrimary() {
        for (int i = 0; i < info.size(); ++i)
            if (info.get(i).primaryKey)
                return true;
        return false;
    }

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

    public String name(int index) {
        return info.get(index).name;
    }

    public int index(String name) {
        return indexOf(name);
    }

    public int type(int index) {
        return info.get(index).type;
    }

    public int type(String fldname) {
        return type(indexOf(fldname));
    }

    public int length(int index) {
        return info.get(index).length;
    }

    public int length(String fldname) {
        return length(indexOf(fldname));
    }

    public boolean notNull(int index) {
        return info.get(index).notNull;
    }

    public boolean notNull(String fldname) {
        return notNull(indexOf(fldname));
    }

    public boolean autoIncrement(int index) {
        return info.get(index).autoIncrement;
    }

    public boolean autoIncrement(String fldname) {
        return autoIncrement(indexOf(fldname));
    }

    public boolean primaryKey(int index) {
        return info.get(index).primaryKey;
    }

    public boolean primaryKey(String fldname) {
        return primaryKey(indexOf(fldname));
    }

    public DataEntity defaultValue(int index) {
        return info.get(index).defaultValue;
    }

    public DataEntity defaultValue(String fldname) {
        return defaultValue(indexOf(fldname));
    }

    public int columnCount() {
        return info.size();
    }

    public void getBytes(ByteBuffer buffer) {
        buffer.putInt(info.size());
        for (int i = 0; i < info.size(); ++i) {
            FieldInfo field = info.get(i);
            field.getBytes(buffer);
        }
    }

    public Schema(ByteBuffer buffer) {
        int len = buffer.getInt();
        for (int i = 0; i < len; ++i)
            info.add(new FieldInfo(buffer));
    }

    class FieldInfo {
        String name;
        int type, length;
        boolean notNull, autoIncrement, primaryKey;
        DataEntity defaultValue;

        public FieldInfo(String name, int type, int length, boolean notNull, boolean autoIncrement,
                boolean primaryKey, DataEntity defaultValue) {
            this.name = name;
            this.type = type;
            this.length = length;
            this.notNull = notNull;
            this.autoIncrement = autoIncrement;
            this.primaryKey = primaryKey;
            this.defaultValue = defaultValue;
        }

        public FieldInfo(ByteBuffer buffer) {
            name = buffer.getString();
            type = buffer.getInt();
            length = buffer.getInt();
            notNull = buffer.getBoolean();
            autoIncrement = buffer.getBoolean();
            primaryKey = buffer.getBoolean();
            defaultValue = DataEntity.fromBytes(buffer);
        }

        public void getBytes(ByteBuffer buffer) {
            buffer.putString(name);
            buffer.putInt(type);
            buffer.putInt(length);
            buffer.putBoolean(notNull);
            buffer.putBoolean(autoIncrement);
            buffer.putBoolean(primaryKey);
            defaultValue.getBytesWithType(buffer);
        }
    }

    public int estimatedLength() {
        int tot = 0;
        for (int i = 0; i < info.size(); ++i) {
            FieldInfo field = info.get(i);
            if (field.type == VARCHAR)
                tot += field.length * 2 / 3;
            else if (field.type == DECIMAL)
                tot += (field.length + 2) * 2;
            else if (field.type == INTEGER)
                tot += 4;
            else if (field.type == BOOLEAN)
                tot += 1;
            else if (field.type == DATE)
                tot += 8;
            else if (field.type == TIMESTAMP)
                tot += 8;
            else if (field.type == FLOAT)
                tot += 8;
            else if (field.type == CHAR)
                tot += field.length * 2 + 4;
        }
        return tot;
    }

    public Collection<String> fields() {
        Collection<String> ret = new LinkedList<String>();
        for (int i = 0; i < info.size(); ++i)
            ret.add(info.get(i).name);
        return ret;
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < info.size(); ++i) {
            s += name(i) + ", type=" + type(i) + ", length=" + length(i) + " " + (notNull(i) ? "not_null " : "")
                    + (autoIncrement(i) ? "auto_increment " : "") + (primaryKey(i) ? "primary_key " : "");
            s += "\n";
        }
        return s;
    }
}
