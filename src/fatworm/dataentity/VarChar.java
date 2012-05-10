package fatworm.dataentity;

import fatworm.util.ByteLib;

public class VarChar extends DataEntity {
    String value;
    public VarChar(String v) {
        value = v;
    }

    public VarChar(byte[] data, int offset) {
        int slen = ByteLib.bytesToInt(data, offset);
        value = ByteLib.bytesToString(data, offset + 4, slen);
    }

    public byte[] getBytes() {
        byte[] data = ByteLib.stringToBytes(value);
        byte[] ret = new byte[data.length + 4];
        ByteLib.intToBytes(data.length, ret, 0);
        System.arraycopy(data, 0, ret, 4, data.length);
        return ret;
    }

    public int compareTo(DataEntity t){
    	if (t instanceof FixChar) {
    		return value.compareTo(((FixChar) t).value);
    	}
    	if (t instanceof VarChar) {
            return value.compareTo(((VarChar)t).value);
        }
        if (t instanceof TimeStamp) {
            return value.compareTo(((TimeStamp)t).value.toString());
        }
        return -1;
    }
	@Override
	public DataEntity opWith(DataEntity o, String op) {
		return new NullDataEntity();
	}
	
		
	public DataEntity toType(int type) {
		if (type == java.sql.Types.CHAR)
			return new FixChar(value, value.length());
		else if (type == java.sql.Types.TIMESTAMP) 
			return new TimeStamp(java.sql.Timestamp.valueOf(value));
		return this;
	}


}
