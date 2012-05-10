package fatworm.dataentity;

import fatworm.util.ByteLib;

public class TimeStamp extends DataEntity
{
    java.sql.Timestamp value;
    public TimeStamp(java.sql.Timestamp v) {
        value = v;
    }

    public TimeStamp(byte[] data, int offset) {
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
		return new NullDataEntity();
	}
}
