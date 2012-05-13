package fatworm.dataentity;

import static java.sql.Types.*;

public class NullDataEntity extends DataEntity
{
    public boolean isNull(){
        return true;
    }

    public byte[] getBytes() {
        return new byte[0];
    }

    public int type() {
        return NULL;
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
