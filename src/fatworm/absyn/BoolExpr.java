package fatworm.absyn;

import java.util.HashSet;
import java.util.Set;

import fatworm.query.Env;
import fatworm.util.Util;
public class BoolExpr {

	public String toString(){
		return "mustn't be echoed, something wrong@BoolExpr";
	}

    public boolean satisfiedBy(Env env){
    	return false;
    }

    public Set<String> dumpUsefulColumns() {
        return new HashSet<String>();
    }

    public Set<String> dumpUsefulFunctions() {
        Set<String> candidate = dumpUsefulColumns();
        Set<String> result = new HashSet<String>();
        for(String column : candidate) {
        	if (Util.isFunction(column))
        		result.add(column);
        }
        return result;
    }
}
