package fatworm.absyn;

import java.util.HashSet;
import java.util.Set;

import fatworm.dataentity.DataEntity;
import fatworm.plantree.Node;
import fatworm.query.Env;
import fatworm.query.Scan;
import fatworm.util.Util;

public class InExpr extends BoolExpr{
	Value val;
	Node query;
    boolean notin;
	public InExpr(Value val, Node query){
		this.val = val;
		this.query = query;
	}
//    public InExpr(Value val, Node query, boolean notin) {
//        this(val, query);
//        this.notin = notin;
//    }
    public boolean satisfiedBy(Env env) {
        DataEntity left = val.getValue(env);
        Scan rightscan = Util.getQueryPlanner().createQueryPlan(query,env).open();
        rightscan.beforeFirst();
//        if (notin) {
//            while(rightscan.next()) {
//                if(Compare.compare(left, rightscan.getColumnByIndex(0),"="))
//                    return false;
//            }
//            return true;
//        }
        while(rightscan.next()) {
        	if(Compare.compare(left,rightscan.getColumnByIndex(0), "="))
        		return true;
        }
        return false;
    }
    
    public Set<String> dumpUsefulColumns() {
    	Set<String> result = new HashSet<String>();
    	result.addAll(val.dumpUsefulColumns());
    	result.addAll(query.dumpUsefulColumns());
    	return result;
    }
}
