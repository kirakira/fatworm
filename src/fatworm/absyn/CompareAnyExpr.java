package fatworm.absyn;

import fatworm.plantree.Node;
import fatworm.query.Env;
import fatworm.query.Scan;
import fatworm.util.Util;
import fatworm.dataentity.DataEntity;

public class CompareAnyExpr extends BoolExpr{
	Value val;
	Node query;
	String cop;
	public CompareAnyExpr(Value val, Node query, String cop){
		this.val = val;
		this.query = query;
		this.cop = cop;
	}

    public boolean satisfiedBy(Env env) {
        DataEntity left = val.getValue(env);
        Scan rightscan = Util.getQueryPlanner().createQueryPlan(query,env).open();
        rightscan.beforeFirst();
        while(rightscan.next()) {
            if(Compare.compare(left, rightscan.getFirstColumn(), cop))
                return true;
        }
        return false;
    }
}
