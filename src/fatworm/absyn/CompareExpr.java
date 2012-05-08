package fatworm.absyn;

public class CompareExpr extends BoolExpr{
	Value left, right;
	String cop;
	public CompareExpr(Value left, Value right, String cop){
		this.left = left;
		this.right = right;
		this.cop = cop;
	}
	public String toString(){
		return left.toString() + " " + cop + " " + right.toString();
	}
}
