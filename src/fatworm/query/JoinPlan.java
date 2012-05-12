package fatworm.query;

import java.util.ArrayList;
import java.util.List;


public class JoinPlan extends QueryPlan{
    List<QueryPlan> planList;

    // public JoinPlan(QueryPlan plan, BoolExpr pred, Env env) {
    //     this.plan = plan;
    //     this.pred = pred;
    //     this.env = env;
    // }

    public JoinPlan(List<QueryPlan> planList) {
        this.planList = planList;
        //    	this(plan, pred, Util.getEmptyEnv());
        // no setFunctionToCalc because no func is calc in Product.
        // no Env because no col can be refered in Product.
    }

    public Scan open() {
        List<Scan> scanList = new ArrayList<Scan>();
        for (QueryPlan plan: planList) {
            scanList.add(plan.open());
        }
        return new JoinScan(scanList);
    }
}