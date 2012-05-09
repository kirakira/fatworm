package fatworm.dataentity;

public class FixChar extends DataEntity{
    String value;
    int length;
    public FixChar(String s, int l){
        value = s;
        length = l;
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
		return null;
	}

}