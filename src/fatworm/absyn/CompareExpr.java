package fatworm.absyn;

import java.util.HashSet;
import java.util.Set;

import fatworm.query.Env;
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


    public boolean satisfiedBy(Env env){
        return Compare.compare(left.getValue(env), right.getValue(env), cop);
    }

    public Set<String> dumpUsefulColumns() {
    	Set<String> result = new HashSet<String>();
    	result.addAll(left.dumpUsefulColumns());
    	result.addAll(right.dumpUsefulColumns());
    	return result;
    }
}
