package fatworm.dataentity;

import fatworm.util.ByteBuffer;

import java.util.Date;
import static java.sql.Types.*;

public class TimeStamp extends DataEntity
{
    java.sql.Timestamp value;

    public TimeStamp() {
        this(new java.sql.Timestamp(new Date().getTime()));
    }

    public TimeStamp(java.sql.Timestamp v) {
        value = v;
    }

    public TimeStamp(ByteBuffer buffer) {
        value = new java.sql.Timestamp(buffer.getLong());
    }

    public void getBytes(ByteBuffer buffer) {
        buffer.putLong(value.getTime());
    }

    public int type() {
        return TIMESTAMP;
    }

    public int estimatedSize() {
        return 8;
    }

    public int compareTo(DataEntity t) {
        if(t instanceof DateTime) {
            return value.compareTo(((DateTime)t).value);
        }
        if(t instanceof TimeStamp) {
            return value.compareTo(((TimeStamp)t).value);
        }
        if(t instanceof FixChar) {
            return value.toString().compareTo(((FixChar)t).value);
        }
        if(t instanceof VarChar) {
            return value.toString().compareTo(((VarChar)t).value);
        }
        return -1;
    }
	@Override
    public DataEntity opWith(DataEntity o, String op) {
		return new NullDataEntity();
	}

    public String toString() {
        return value.toString();
    }
	
	public DataEntity toType(int type) {
		String s = value.toString();
		s = s.substring(0, s.indexOf("."));
		if (type == java.sql.Types.CHAR) 
			return new FixChar(s, s.length());
		else if (type == java.sql.Types.VARCHAR)
			return new VarChar(s);
		return this;
	}

    public Object toJavaType() {
    	return value;
    }	
}
