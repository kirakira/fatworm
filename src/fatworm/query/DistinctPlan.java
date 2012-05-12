package fatworm.query;

public class DistinctPlan extends QueryPlan {

	QueryPlan plan;
	public DistinctPlan(QueryPlan plan) {
		this.plan = plan;
	}
	@Override
	public Scan open() {
		return new DistinctScan(plan.open());
	}
}
