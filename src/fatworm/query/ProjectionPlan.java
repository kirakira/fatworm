package fatworm.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fatworm.absyn.ProjectionRenameValue;
import fatworm.absyn.ProjectionValue;
import fatworm.util.Util;

public class ProjectionPlan extends QueryPlan{
    
    QueryPlan plan;
    List<ProjectionValue> projections;
    Env env;
    Map<String, String> realname = new HashMap<String, String>();
    public ProjectionPlan(QueryPlan plan, List<ProjectionValue> projection, Env env) {
        this.plan = plan;
        this.projections = projection;
        this.env = env;
        funcSet = new HashSet<String>();
        for (ProjectionValue proj: projections)
        	funcSet.addAll(proj.dumpUsefulFunctions());
        if (plan != null) 
        	plan.addFunctionsToCalc(this.funcSet);
        for (ProjectionValue proj: this.projections) {
            if (proj instanceof ProjectionRenameValue) {
                realname.put(((ProjectionRenameValue) proj).alias, proj.val.toString());
            }
        }
    }
    
    public ProjectionPlan(QueryPlan plan, List<ProjectionValue> proj) {
    	this(plan, proj, Util.getEmptyEnv());
    }

    public Scan open() {
    	if (plan == null)
    		return new ProjectionScan(new OneTimeScan(true), projections, env);
    	Scan scan = new ProjectionScan(plan.open(), projections, env);
    	scan.setRealName(realname);
        return scan;
    }

    public void addFunctionsToCalc(Set<String> funcs){
    	funcSet.addAll(funcs);
        plan.addFunctionsToCalc(funcs);
    }

}