package fatworm.dataentity;


public class TimeStamp extends DataEntity
{
    java.sql.Timestamp value;
    public TimeStamp(java.sql.Timestamp v) {
        value = v;
    }
    public int compareTo(DataEntity t) {
        return 0;
    }
}