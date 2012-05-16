package fatworm.dataentity;

import fatworm.util.ByteBuffer;

import static java.sql.Types.*;

public abstract class DataEntity
{
    public abstract int compareTo(DataEntity o); 
    public abstract DataEntity opWith(DataEntity o, String op);
    public boolean isNull() {
        return false;
    }

    public abstract void getBytes(ByteBuffer buffer);

    public void getBytesWithType(ByteBuffer buffer) {
        buffer.putInt(type());
        getBytes(buffer);
    }
    
    public DataEntity toType(int type) {
    	return this;
    }

    public abstract int estimatedSize();

    public abstract int type();

    public static DataEntity fromBytes(ByteBuffer buffer) {
        int type = buffer.getInt();
        return fromBytes(type, buffer);
    }

    public static DataEntity fromBytes(int type, ByteBuffer buffer) {
        DataEntity ret = null;
        switch (type) {
            case INTEGER:
                ret = new Int(buffer);
                break;

            case FLOAT:
                ret = new fatworm.dataentity.Float(buffer);
                break;

            case BOOLEAN:
                ret = new Bool(buffer);
                break;

            case CHAR:
                ret = new FixChar(buffer);
                break;

            case VARCHAR:
                ret = new VarChar(buffer);
                break;

            case DATE:
                ret = new DateTime(buffer);
                break;

            case TIMESTAMP:
                ret = new TimeStamp(buffer);
                break;

            case DECIMAL:
                ret = new Decimal(buffer);
                break;

            case NULL:
                ret = new NullDataEntity();
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
