package fatworm.dataentity;

public class GreaterThanEqualToComparator extends DataComparator {
    DataComparator lt = new GreaterThanComparator(), eq = new EqualToComparator();

    public boolean compare(DataEntity a, DataEntity b) {
        return (lt.compare(a, b) || eq.compare(a, b));
    }
}
