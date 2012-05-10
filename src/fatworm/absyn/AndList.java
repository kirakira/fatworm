package fatworm.absyn;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import fatworm.query.Env;

public class AndList extends BoolExpr{
	public LinkedList<BoolExpr> andList = null;
	public AndList(LinkedList<BoolExpr> andList){
		this.andList = andList;
	}

	public String toString(){
		String ands = "";
		for (BoolExpr expr: andList){
			if (ands != "") ands += "&";
			ands += expr.toString();
		}
		return ands;
	}
    public boolean satisfiedBy(Env env) {
        for (BoolExpr expr : andList) {
            if (!expr.satisfiedBy(env))
                return false;
        }
        return true;
    }

    public Set<String> dumpUsefulColumns() {
    	Set<String> result = new HashSet<String>();
    	for(BoolExpr expr: andList) {
    		result.addAll(expr.dumpUsefulColumns());
    	}
    	return result;
    }
}
