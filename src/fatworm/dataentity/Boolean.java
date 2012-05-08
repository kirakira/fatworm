package fatworm.dataentity;

public class Boolean extends DataEntity
{
    boolean value;
    public Boolean(boolean v) {
        value = v;
    }
    public int compareTo(DataEntity t) {
        return 0;
    }
}