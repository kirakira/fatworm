package fatworm.dataentity;

import fatworm.util.ByteLib;

public class Float extends DataEntity
{
    double value;
    public Float(double v) {
        value = v;
    }

    public Float(byte[] data, int offset) {
        value = ByteLib.bytesToDouble(data, offset);
    }

    public byte[] getBytes() {
        byte[] ret = new byte[8];
        ByteLib.doubleToBytes(value, ret, 0);
        return ret;
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
}
