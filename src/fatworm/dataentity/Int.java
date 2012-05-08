package fatworm.dataentity;

public class Int extends DataEntity
{
    int value;
    public Int(int v) {
        value = v;
    }
    public int compareTo(DataEntity t) {
        return 0;
    }
}