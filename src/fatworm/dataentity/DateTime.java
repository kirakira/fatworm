package fatworm.dataentity;

import fatworm.util.ByteLib;

public class DateTime extends DataEntity
{
    java.sql.Timestamp value;
    public DateTime(java.sql.Timestamp v) {
        value = v;
    }

    public DateTime(byte[] data, int offset) {
        value = new java.sql.Timestamp(ByteLib.bytesToLong(data, offset));
    }

    public byte[] getBytes() {
        byte[] ret = new byte[8];
        ByteLib.longToBytes(value.getTime(), ret, 0);
        return ret;
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
		// TODO Auto-generated method stub
		return new NullDataEntity();
	}

    public String toString() {
        return value.toString();
    }
	public DataEntity toType(int type) {
		if (type == java.sql.Types.CHAR)
			return new FixChar(value.toString(), value.toString().length());
		else if (type == java.sql.Types.VARCHAR)
			return new VarChar(value.toString());
		return this;
	}
}
