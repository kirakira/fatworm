package fatworm.query;

import java.util.List;
import java.util.Set;

import fatworm.absyn.OrderByColumn;

public class OrderPlan extends QueryPlan {
	
	public QueryPlan plan;
	List<OrderByColumn> order;
	
	public OrderPlan(QueryPlan plan, List<OrderByColumn> order, Env env) {
		this.plan = plan;
		this.order = order;
	}
	
	public OrderPlan(QueryPlan plan, List<OrderByColumn> order) {
		this(plan, order, null);
	}
		
	@Override
	public Scan open() {
		return new OrderScan(plan.open(), order);
	}
	
    public void addFunctionsToCalc(Set<String> funcs){
    	funcSet.addAll(funcs);
        plan.addFunctionsToCalc(funcs);
    }	
}
