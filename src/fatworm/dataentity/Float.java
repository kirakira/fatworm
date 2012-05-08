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
	@Override
	public DataEntity opWith(DataEntity o, String op) {
		// TODO Auto-generated method stub
		return null;
	}
}