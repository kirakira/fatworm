package fatworm.dataentity;

public class VarChar extends DataEntity {
    String value;
    public VarChar(String v) {
        value = v;
    }
    public int compareTo(DataEntity t){
        return 0;
    }
	@Override
	public DataEntity opWith(DataEntity o, String op) {
		// TODO Auto-generated method stub
		return null;
	}
}