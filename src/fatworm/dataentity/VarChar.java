package fatworm.dataentity;

import fatworm.util.ByteBuffer;

import static java.sql.Types.*;

public class VarChar extends DataEntity {
    String value;

    public VarChar(String v) {
        value = v;
    }

    public VarChar(ByteBuffer buffer) {
        value = buffer.getString();
    }

    public void getBytes(ByteBuffer buffer) {
        buffer.putString(value);
    }

    public int type() {
        return VARCHAR;
    }

    public int estimatedSize() {
        return 2 * value.length() + 4;
    }

    public int compareTo(DataEntity t){
    	if (t instanceof FixChar) {
    		return value.compareToIgnoreCase(((FixChar) t).value);
    	}
    	if (t instanceof VarChar) {
            return value.compareToIgnoreCase(((VarChar)t).value);
        }
        if (t instanceof TimeStamp) {
            return value.compareToIgnoreCase(((TimeStamp)t).value.toString());
        }
        if (t instanceof Int) {
            return - t.compareTo(this);
        }
        return -1;
    }
	@Override
	public DataEntity opWith(DataEntity o, String op) {
		return new NullDataEntity();
	}

    public String toString() {
        return value;
    }
	
	public DataEntity toType(int type) {
		if (type == java.sql.Types.CHAR)
			return new FixChar(value, value.length());
		else if (type == java.sql.Types.TIMESTAMP) {
			return new TimeStamp(java.sql.Timestamp.valueOf(value.replaceAll("'", "")));
			
		}
		else if (type == java.sql.Types.INTEGER) {
		    return new Int(Integer.valueOf(value));
		}
		return this;
	}

	public Object toJavaType() {
    	return value;
    }	
	
}
