package fatworm.absyn;

public class OpValue extends Value{
	public String op;
	public Value left, right;
	public OpValue(String op, Value left, Value right){
		this.op = op;
		this.left = left;
		this.right = right;
	}
	
}
