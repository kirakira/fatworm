package fatworm.dataentity;

public class Float extends DataEntity
{
    double value;
    public Float(double v) {
        value = v;
    }
    public int compareTo(DataEntity t) {
        return 0;
    }
}