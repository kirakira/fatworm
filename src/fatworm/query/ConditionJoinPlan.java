package fatworm.query;

import java.util.ArrayList;
import java.util.List;

public class ConditionJoinPlan extends JoinPlan {

	public ConditionJoinPlan(List<QueryPlan> planList) {
		super(planList);
	}

    public Scan open() {
        List<Scan> scanList = new ArrayList<Scan>();
        for (QueryPlan plan: planList) {
            scanList.add(plan.open());
        }
        return new ConditionJoinScan(scanList);
    }	
}
