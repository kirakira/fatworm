package fatworm.dataentity;

import fatworm.util.ByteBuffer;

import static java.sql.Types.*;

public class Bool extends DataEntity
{
    boolean value;
    public Bool(boolean v) {
        value = v;
    }

    public Bool(ByteBuffer buffer) {
        value = buffer.getBoolean();
    }

    public void getBytes(ByteBuffer buffer) {
        buffer.putBoolean(value);
    }

    public int type() {
        return BOOLEAN;
    }

    public int estimatedSize() {
        return 1;
    }

    public int compareTo(DataEntity t) {
        if (t instanceof Bool) {
        	if (value == ((Bool)t).value)
        		return 0;
        }
        return -1;
    }
	@Override
	public DataEntity opWith(DataEntity o, String op) {
		return new NullDataEntity();
	}

    public String toString() {
        return new Boolean(value).toString();
    }
    
    public Object toJavaType() {
    	return value;
    }
}
