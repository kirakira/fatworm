package fatworm.dataentity;

import static java.sql.Types.*;

public class Bool extends DataEntity
{
    boolean value;
    public Bool(boolean v) {
        value = v;
    }

    public Bool(byte[] data, int offset) {
        if (data[offset] == 0)
            value = false;
        else
            value = true;
    }

    public byte[] getBytes() {
        byte[] ret = new byte[1];
        if (value)
            ret[0] = 1;
        else
            ret[0] = 0;
        return ret;
    }

    public int type() {
        return BOOLEAN;
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
