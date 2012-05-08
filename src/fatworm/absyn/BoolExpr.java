package fatworm.absyn;

import fatworm.query.Env;
public class BoolExpr {

	public String toString(){
		return "mustn't be echoed, something wrong@BoolExpr";
	}

    public boolean satisfiedBy(Env env){
    	return false;
    }

}
