package fatworm.dataentity;

import java.math.BigDecimal;

import fatworm.util.ByteBuffer;
import static java.sql.Types.*;

public class Float extends DataEntity
{
    double value;
    public Float(double v) {
        value = v;
    }

    public Float(ByteBuffer buffer) {
        value = buffer.getDouble();
    }

    public void getBytes(ByteBuffer buffer) {
        buffer.putDouble(value);
    }

    public int type() {
        return FLOAT;
    }

    public int estimatedSize() {
        return 8;
    }

    public int compareTo(DataEntity t) {
        if(t instanceof Float) {
            double anotherv = ((Float)t).value;
            if(value < anotherv)
                return -1;
            if(value > anotherv)
                return 1;
            return 0;
        }

        if(t instanceof Int) {
            return -t.compareTo(this);
        }

        if(t instanceof Decimal) {
            return -t.compareTo(this);
        }
        return -1;
    }
	@Override
	public DataEntity opWith(DataEntity t, String op) {
        if(t instanceof Int) {
            int anotherv = ((Int)t).value;
            if (op.equals("+"))
                return new Float(value+anotherv);
            if (op.equals("-"))
                return new Float(value-anotherv);
            if (op.equals("*"))
                return new Float(value*anotherv);
            if (op.equals("/"))
                return new Float(value/anotherv);
        }
        if(t instanceof Float) {
            double anotherv = ((Float)t).value;
            if (op.equals("+"))
                return new Float(value+anotherv);
            if (op.equals("-"))
                return new Float(value-anotherv);
            if (op.equals("*"))
                return new Float(value*anotherv);
            if (op.equals("/"))
                return new Float(value/anotherv);
        }
        if(t instanceof Decimal) {
            double anotherv = ((Decimal)t).value.doubleValue();
            if (op.equals("+"))
                return new Float(value+anotherv);
            if (op.equals("-"))
                return new Float(value-anotherv);
            if (op.equals("*"))
                return new Float(value*anotherv);
            if (op.equals("/"))
                return new Float(value/anotherv);
        }
		return new NullDataEntity();

	}

    public String toString() {
        return new Double(value).toString();
    }
	
	public DataEntity toType(int type) {
		if (type == java.sql.Types.INTEGER)
			return new Int((int)value);
		else if (type == java.sql.Types.DECIMAL)
			return new Decimal(new BigDecimal(value));
		else 
			return this;
	}
	
    public Object toJavaType() {
    	return value;
    }	
	
}
