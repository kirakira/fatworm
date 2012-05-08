package fatworm.absyn;

public class ConstValue extends Value{
	public String val;
	public ConstValue(String val){
		this.val = val;
	}
	public String toString(){
		return val;
	}
}
