package fatworm.dataentity;

import fatworm.util.ByteLib;

import static java.sql.Types.*;

public abstract class DataEntity
{
    public abstract int compareTo(DataEntity o); 
    public abstract DataEntity opWith(DataEntity o, String op);
    public boolean isNull() {
        return false;
    }

    public abstract byte[] getBytes();

    public byte[] getBytesWithType() {
        byte[] data = getBytes();
        byte[] ret = new byte[data.length + 4];
        ByteLib.intToBytes(type(), ret, 0);
        System.arraycopy(data, 0, ret, 4, data.length);
        return ret;
    }
    
    public DataEntity toType(int type) {
    	return this;
    }

    public abstract int estimatedSize();

    public static DataEntity fromBytes(byte[] data, int offset) {
        int type = ByteLib.bytesToInt(data, offset);
        return fromBytes(type, data, offset + 4);
    }

    public abstract int type();

    public static DataEntity fromBytes(int type, byte[] data, int offset) {
        DataEntity ret = null;
        switch (type) {
            case INTEGER:
                ret = new Int(data, offset);
                break;

            case FLOAT:
                ret = new fatworm.dataentity.Float(data, offset);
                break;

            case BOOLEAN:
                ret = new Bool(data, offset);
                break;

            case CHAR:
                ret = new FixChar(data, offset);
                break;

            case VARCHAR:
                ret = new VarChar(data, offset);
                break;

            case DATE:
                ret = new DateTime(data, offset);
                break;

            case TIMESTAMP:
                ret = new TimeStamp(data, offset);
                break;

            case DECIMAL:
                ret = new Decimal(data, offset);
                break;

            default:
                System.err.println("Unsupported data type: " + type + "; ignored");
                ret = null;
                break;
        }
        return ret;
    }
    
    public boolean equals(Object o) {
    	if (o == null)
    		return false;
    	return this.toString().equals(o.toString());
    }
    
    public int hashCode() {
    	return this.toString().hashCode();
    }
    
    public Object toJavaType() {
    	return null;
    }
}
