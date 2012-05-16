package fatworm.query;

import java.util.Set;

import fatworm.absyn.BoolExpr;
import fatworm.util.Util;

public class SelectPlan extends QueryPlan{
    QueryPlan plan;
    BoolExpr pred;
    Env env;
    public SelectPlan(QueryPlan plan, BoolExpr pred, Env env) {
        this.plan = plan;
        this.pred = pred;
        this.env = env;
        funcSet.addAll(pred.dumpUsefulFunctions());
        if (plan != null)
        	plan.addFunctionsToCalc(this.funcSet);
    }

    public SelectPlan(QueryPlan plan, BoolExpr pred) {
    	this(plan, pred, Util.getEmptyEnv());
    }

    public Scan open() {
    	if (plan == null)
    		return new OneTimeScan(pred.satisfiedBy(env)); 
    	
    	Scan scan = plan.open();
    	if (scan instanceof ConditionJoinScan) {
    	    ConditionJoinScan condjoin = (ConditionJoinScan)scan;
    	    BoolExpr newpred = condjoin.setCondition(pred);
    	    if (newpred != null)
    	        return new SelectScan(condjoin, newpred, env);
    	    else
    	        return condjoin;
    	}
    	return new SelectScan(scan, pred, env);
    }

    public void addFunctionsToCalc(Set<String> funcs){
    	funcSet.addAll(funcs);
    	if (plan != null)
    		plan.addFunctionsToCalc(funcs);
    }

}