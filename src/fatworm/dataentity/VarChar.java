package fatworm.dataentity;

public class VarChar extends DataEntity {
    String value;
    public VarChar(String v) {
        value = v;
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
}