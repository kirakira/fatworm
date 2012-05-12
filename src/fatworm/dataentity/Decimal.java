package fatworm.dataentity;

import java.math.BigDecimal;
import fatworm.util.ByteLib;

public class Decimal extends DataEntity {
    
    BigDecimal value;
    public Decimal(BigDecimal v) {
        value = v;
    }

    public Decimal(byte[] data, int offset) {
        int slen = ByteLib.bytesToInt(data, offset);
        value = new BigDecimal(ByteLib.bytesToString(data, offset + 4, slen));
    }

    public byte[] getBytes() {
        byte[] data = ByteLib.stringToBytes(value.toPlainString());
        byte[] ret = new byte[data.length + 4];
        ByteLib.intToBytes(data.length, ret, 0);
        System.arraycopy(data, 0, ret, 4, data.length);
        return ret;
    }
    
    public int compareTo(DataEntity t) {
        if(t instanceof Decimal) {
            return value.compareTo(((Decimal)t).value);
        }
        if(t instanceof Float) {
            return value.compareTo(new BigDecimal(((Float)t).value));
        }
        if(t instanceof Int) {
            return value.compareTo(new BigDecimal(((Int)t).value));
        }
        return 0;
    }

	@Override
	public DataEntity opWith(DataEntity t, String op) {
        if(t instanceof Decimal) {
            if (op.equals("+"))
                return new Decimal(value.add(((Decimal)t).value));
            if (op.equals("-"))
                return new Decimal(value.subtract(((Decimal)t).value));
            if (op.equals("*"))
                return new Decimal(value.multiply(((Decimal)t).value));
            if (op.equals("/"))
                return new Decimal(value.divide(((Decimal)t).value));
            if (op.equals("%"))
                return new Decimal(value.remainder(((Decimal)t).value));
        }
        if(t instanceof Int) {
            if (op.equals("+"))
                return new Decimal(value.add(new BigDecimal(((Int)t).value)));
            if (op.equals("-"))
                return new Decimal(value.subtract(new BigDecimal(((Int)t).value)));
            if (op.equals("*"))
                return new Decimal(value.multiply(new BigDecimal(((Int)t).value)));
            if (op.equals("/"))
                return new Decimal(value.divide(new BigDecimal(((Int)t).value)));
            if (op.equals("%"))
                return new Decimal(value.remainder(new BigDecimal(((Int)t).value)));
        }

        if(t instanceof Float) {
            if (op.equals("+"))
                return new Float(value.doubleValue() + ((Float)t).value);
            if (op.equals("-"))
                return new Float(value.doubleValue() - ((Float)t).value);
            if (op.equals("*"))
                return new Float(value.doubleValue() * ((Float)t).value);
            if (op.equals("/"))
                return new Float(value.doubleValue() / ((Float)t).value);
        }
		return new NullDataEntity();
	}

    public String toString() {
        return value.toString();
    }
	
	public DataEntity toType(int type) {
		if (type == java.sql.Types.INTEGER)
			return new Int(value.intValue());
		else if (type == java.sql.Types.FLOAT)
			return new Float(value.doubleValue());
		else 
			return this;
	}
	
    public Object toJavaType() {
    	return value;
    }	
}
