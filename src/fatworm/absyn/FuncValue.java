package fatworm.absyn;

public class FuncValue extends Value{
	String func;
	Value val;
	public FuncValue(String func, Value val){
		this.func = func;
		this.val = val;
	}
	public String toString(){
		return func+"("+val.toString()+")";
	}
}
