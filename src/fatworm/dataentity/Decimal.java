package fatworm.dataentity;

import java.math.BigDecimal;

public class Decimal extends DataEntity {
    
    BigDecimal value;
    public Decimal(BigDecimal v) {
        value = v;
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
		return null;
	}
}