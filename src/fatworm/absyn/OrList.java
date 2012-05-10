package fatworm.absyn;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import fatworm.query.Env;

public class OrList extends BoolExpr{
	public LinkedList<BoolExpr> orList = null;
	public OrList(LinkedList<BoolExpr> orList){
		this.orList = orList;
	}

	public String toString(){
		String ors = "";
		for (BoolExpr expr: orList){
			if (ors != "") ors += "|";
			ors += expr.toString();
		}
		return ors;
	}

    public boolean satisfiedBy(Env env) {
        for (BoolExpr expr : orList) {
            if (expr.satisfiedBy(env))
                return true;
        }
        return false;
    }
    
    public Set<String> dumpUsefulColumns() {
    	Set<String> result = new HashSet<String>();
    	for(BoolExpr expr: orList) {
    		result.addAll(expr.dumpUsefulColumns());
    	}
    	return result;
    }

}
