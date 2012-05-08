package fatworm.absyn;

public class StringValue extends Value{
	public String s;
	public StringValue(String s){
		this.s = s;
	}
	public String toString(){
		return s;
	}
}
