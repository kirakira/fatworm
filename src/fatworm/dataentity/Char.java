package fatworm.dataentity;

public class Char extends DataEntity{
    String value;
    int length;
    public Char(String s, int l){
        value = s;
        length = l;
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