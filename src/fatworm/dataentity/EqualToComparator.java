package fatworm.dataentity;

public class EqualToComparator extends DataComparator {
    public boolean compare(DataEntity a, DataEntity b) {
        if (a.isNull() || b.isNull())
            return false;
        return (a.compareTo(b) == 0);
    }
}
