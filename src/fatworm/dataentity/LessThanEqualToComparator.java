package fatworm.dataentity;

public class LessThanEqualToComparator extends DataComparator {
    DataComparator lt = new LessThanComparator(), eq = new EqualToComparator();

    public boolean compare(DataEntity a, DataEntity b) {
        return (lt.compare(a, b) || eq.compare(a, b));
    }
}
