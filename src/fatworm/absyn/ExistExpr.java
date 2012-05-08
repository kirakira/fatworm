package fatworm.absyn;

import fatworm.plantree.Node;
import fatworm.query.Env;
import fatworm.query.Scan;
import fatworm.util.Util;

public class ExistExpr extends BoolExpr{
	boolean empty; // true for "NotExist" false for "Exist"
	Node query;
	public ExistExpr(boolean empty, Node query){
		this.empty = empty;
		this.query = query;
	}
    public boolean satisfiedBy(Env env) {
        Scan rightscan = Util.getQueryPlanner().createQueryPlan(query,env).open();
        rightscan.beforeFirst();
        if(rightscan.next())
            return !empty;
        else 
            return empty;
    }
}
