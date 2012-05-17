package fatworm.storage;

import java.util.Map;
import static java.sql.Types.*;

import fatworm.util.ByteBuffer;
import fatworm.dataentity.*;
import fatworm.record.Schema;

public class Tuple {
    private DataEntity[] values;

    private Tuple() {
    }

    public static Tuple create(DataEntity[] values) {
        Tuple ret = new Tuple();
        ret.values = values;
        return ret;
    }

    public static Tuple create(Schema schema, Map<String, DataEntity> map) {
        Tuple ret = new Tuple();

        int len = schema.columnCount();
        ret.values = new DataEntity[len];

        int count = 0;
        for (int i = 0; i < len; ++i) {
            DataEntity de = map.get(schema.name(i));
            if (de == null) {
                if (schema.type(i) == TIMESTAMP)
                    de = new TimeStamp();
                else
                    de = schema.defaultValue(i);
            }
            ++count;
            ret.values[i] = de;
        }
        if (count != schema.columnCount())
            return null;

        return ret;
    }

    public static Tuple create(Schema schema, Map<String, DataEntity> map, DataEntity[] base) {
        Tuple ret = new Tuple();

        int len = schema.columnCount();
        ret.values = new DataEntity[len];

        int count = 0;
        for (int i = 0; i < len; ++i) {
            DataEntity de = map.get(schema.name(i));
            if (de == null)
                de = base[i];
            ++count;
            ret.values[i] = de;
        }
        if (count != schema.columnCount())
            return null;

        return ret;
    }

    public Tuple(ByteBuffer buffer) {
        int len = buffer.getInt();
        values = new DataEntity[len];
        for (int i = 0; i < len; ++i)
            values[i] = DataEntity.fromBytes(buffer);
    }

    public void getBytes(ByteBuffer buffer) {
        buffer.putInt(values.length);
        for (int i = 0; i < values.length; ++i) {
            values[i].getBytesWithType(buffer);
        }
    }

    public DataEntity get(int index) {
        return values[index];
    }

    public DataEntity[] tuple() {
        return values;
    }
}
