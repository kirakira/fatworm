package fatworm.dataentity;

import fatworm.util.ByteBuffer;

import static java.sql.Types.*;

public class FixChar extends DataEntity{
    String value;
    int length;

    public FixChar(String s, int l){
        value = s;
        length = l;
    }

    public FixChar(ByteBuffer buffer) {
        length = buffer.getInt();
        value = buffer.getString();
    }

    public void getBytes(ByteBuffer buffer) {
        buffer.putInt(length);
        buffer.putString(value);
    }

    public int type() {
        return CHAR;
    }

    public int estimatedSize() {
        return length + 4;
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
        if (t instanceof DateTime) {
            return value.compareToIgnoreCase(((DateTime)t).value.toString());
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
		if (type == java.sql.Types.VARCHAR)
			return new VarChar(value);
		else if (type == java.sql.Types.TIMESTAMP) 
			return new TimeStamp(java.sql.Timestamp.valueOf(value));
		else if (type == java.sql.Types.INTEGER) {
		    return new Int(Integer.valueOf(value));
		}		
		return this;
	}
    public Object toJavaType() {
    	return value;
    }	
}
