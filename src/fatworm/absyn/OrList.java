package fatworm.absyn;

import java.util.LinkedList;

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

}
