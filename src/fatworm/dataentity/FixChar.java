package fatworm.dataentity;

import fatworm.util.ByteLib;

public class FixChar extends DataEntity{
    String value;
    int length;
    public FixChar(String s, int l){
        value = s;
        length = l;
    }

    public FixChar(byte[] data, int offset) {
        length = ByteLib.bytesToInt(data, offset);
        int slen = ByteLib.bytesToInt(data, offset + 4);
        value = ByteLib.bytesToString(data, offset + 8, slen);
    }

    public byte[] getBytes() {
        byte[] data = ByteLib.stringToBytes(value);
        byte[] ret = new byte[data.length + 8];
        ByteLib.intToBytes(length, ret, 0);
        ByteLib.intToBytes(data.length, ret, 4);
        System.arraycopy(data, 0, ret, 8, data.length);
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
        if (t instanceof DateTime) {
            return value.compareTo(((DateTime)t).value.toString());
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

}
