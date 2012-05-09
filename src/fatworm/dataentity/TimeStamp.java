package fatworm.dataentity;


public class TimeStamp extends DataEntity
{
    java.sql.Timestamp value;
    public TimeStamp(java.sql.Timestamp v) {
        value = v;
    }
    public int compareTo(DataEntity t) {
        if(t instanceof DateTime) {
            return value.compareTo(((DateTime)t).value);
        }
        if(t instanceof TimeStamp) {
            return value.compareTo(((TimeStamp)t).value);
        }
        if(t instanceof FixChar) {
            return value.toString().compareTo(((FixChar)t).value);
        }
        if(t instanceof VarChar) {
            return value.toString().compareTo(((VarChar)t).value);
        }
        return -1;
    }
	@Override
    public DataEntity opWith(DataEntity o, String op) {
		// TODO Auto-generated method stub
		return null;
	}
}