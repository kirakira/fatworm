package fatworm.dataentity;

public abstract class DataEntity
{
    public abstract int compareTo(DataEntity o); 
    public abstract DataEntity opWith(DataEntity o, String op);
    public boolean isNull() {
        return false;
    }

    public abstract byte[] getBytes();
    
    public DataEntity toType(int type){
    	return this;
    }
}
