package fatworm.absyn;

import fatworm.query.Env;
public class CompareExpr extends BoolExpr{
	Value left, right;
	String cop;

	public CompareExpr(Value left, Value right, String cop){
		this.left = left;
		this.right = right;
		this.cop = cop;
	}

    public boolean satisfiedBy(Env env){
        return Compare.compare(left.getValue(env), right.getValue(env), cop);
    }
}
