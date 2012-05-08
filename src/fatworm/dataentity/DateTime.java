package fatworm.dataentity;


public class DateTime extends DataEntity
{
    java.sql.Timestamp value;
    public DateTime(java.sql.Timestamp v) {
        value = v;
    }
    public int compareTo(DataEntity t) {
        return 0;
    }
}