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
        plan.addFunctionsToCalc(this.funcSet);
    }

    public SelectPlan(QueryPlan plan, BoolExpr pred) {
    	this(plan, pred, Util.getEmptyEnv());
    }

    public Scan open() {
        return new SelectScan(plan.open(), pred, env);
    }

    public void addFunctionsToCalc(Set<String> funcs){
    	funcSet.addAll(funcs);
        plan.addFunctionsToCalc(funcs);
    }

}