package fatworm.dataentity;

import java.math.BigDecimal;

import fatworm.util.ByteBuffer;
import static java.sql.Types.*;

public class Int extends DataEntity
{
    int value;
    public Int(int v) {
        value = v;
    }

    public Int(ByteBuffer buffer) {
        value = buffer.getInt();
    }

    public void getBytes(ByteBuffer buffer) {
        buffer.putInt(value);
    }

    public int type() {
        return INTEGER;
    }

    public int estimatedSize() {
        return 4;
    }

    public int compareTo(DataEntity t) {
        if(t instanceof Int) {
            int anotherv = ((Int)t).value;
            if(value == anotherv)
                return 0;
            if(value < anotherv)
                return -1;
            return 1;
        }

        if(t instanceof Float) {
            double anotherv = ((Float)t).value;
            if(value == anotherv)
                return 0;
            if(value < anotherv)
                return -1;
            return 1;
        }

        if(t instanceof Decimal) {
            return -t.compareTo(this);
        }

        if(t instanceof VarChar) {
            int anotherv = Integer.valueOf(((VarChar)t).value);
            if(value == anotherv)
                return 0;
            if(value < anotherv)
                return -1;
            return 1;            

        }
        
        if(t instanceof FixChar) {
            int anotherv = Integer.valueOf(((VarChar)t).value);
            if(value == anotherv)
                return 0;
            if(value < anotherv)
                return -1;
            return 1;            
        }        
        return -1;
    }
	@Override
	public DataEntity opWith(DataEntity t, String op) {
        if(t instanceof Int) {
            int anotherv = ((Int)t).value;
            if (op.equals("+"))
                return new Int(value+anotherv);
            if (op.equals("-"))
                return new Int(value-anotherv);
            if (op.equals("*"))
                return new Int(value*anotherv);
            if (op.equals("/"))
                return new Float(value*1.0/anotherv);
            if (op.equals("%"))
                return new Int(value%anotherv);
        }
        if(t instanceof Float) {
            double anotherv = ((Float)t).value;
            if (op.equals("+"))
                return new Float(value+anotherv);
            if (op.equals("-"))
                return new Float(value-anotherv);
            if (op.equals("*"))
                return new Float(value *anotherv);
            if (op.equals("/"))
                return new Float(value/anotherv);
        }
        if(t instanceof Decimal) {
            if (op.equals("+"))
                return new Decimal((new BigDecimal(value)).add(((Decimal)t).value));
            if (op.equals("-"))
                return new Decimal((new BigDecimal(value)).subtract(((Decimal)t).value));
            if (op.equals("*"))
                return new Decimal((new BigDecimal(value)).multiply(((Decimal)t).value));
            if (op.equals("/"))
                return new Decimal((new BigDecimal(value)).divide(((Decimal)t).value));
            if (op.equals("%"))
                return new Decimal((new BigDecimal(value)).remainder(((Decimal)t).value));
        }
		return new NullDataEntity();
	}

    public String toString() {
        return new Integer(value).toString();
    }

	public DataEntity toType(int type) {
		if (type == java.sql.Types.FLOAT)
			return new Float(value);
		else if (type == java.sql.Types.DECIMAL)
			return new Decimal(new BigDecimal(value));
		else 
			return this;
	}
	
    public Object toJavaType() {
    	return value;
    }
}
