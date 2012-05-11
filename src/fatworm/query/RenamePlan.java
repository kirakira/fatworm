package fatworm.query;


public class RenamePlan extends QueryPlan{
    String alias;
    QueryPlan plan;
    public RenamePlan(QueryPlan plan, String name) {
        this.alias = name;
        this.plan = plan;
    }
	@Override
	public Scan open() {
		return new RenameScan(plan.open(), alias);
	}
}