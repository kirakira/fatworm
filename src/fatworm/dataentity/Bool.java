package fatworm.dataentity;

public class Bool extends DataEntity
{
    boolean value;
    public Bool(boolean v) {
        value = v;
    }
    public int compareTo(DataEntity t) {
        if (t instanceof Bool) {
        	if (value == ((Bool)t).value)
        		return 0;
        }
        return -1;
    }
	@Override
	public DataEntity opWith(DataEntity o, String op) {
		return null;
	}
}