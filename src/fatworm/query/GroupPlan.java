package fatworm.query;

public class GroupPlan extends QueryPlan {

	QueryPlan plan;
	String keyName;
	public GroupPlan(QueryPlan plan, String keyName) {
		this.plan = plan;
		this.keyName = keyName;
	}
	@Override
	public Scan open() {
		return new GroupScan(plan.open(), keyName, funcSet);
	}

}
