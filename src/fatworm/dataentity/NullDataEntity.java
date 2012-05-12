package fatworm.dataentity;


public class NullDataEntity extends DataEntity
{
    public boolean isNull(){
        return true;
    }

    public byte[] getBytes() {
        return null;
    }

	@Override
	public int compareTo(DataEntity o) {
		return 0;
	}
	@Override
	public DataEntity opWith(DataEntity o, String op) {
		return new NullDataEntity();
	}
	
	public String toString() {
		return "null";
	}
	
    public Object toJavaType() {
    	return null;
    }	
	
}
