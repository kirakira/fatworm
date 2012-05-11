package fatworm.query;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fatworm.absyn.ProjectionValue;
import fatworm.util.Util;

public class ProjectionPlan extends QueryPlan{
    
    QueryPlan plan;
    List<ProjectionValue> projections;
    Env env;
    public ProjectionPlan(QueryPlan plan, List<ProjectionValue> projection, Env env) {
        this.plan = plan;
        this.projections = projection;
        this.env = env;
        funcSet = new HashSet<String>();
        for (ProjectionValue proj: projections)
        	funcSet.addAll(proj.dumpUsefulFunctions());
        plan.addFunctionsToCalc(this.funcSet);
    }
    
    public ProjectionPlan(QueryPlan plan, List<ProjectionValue> proj) {
    	this(plan, proj, Util.getEmptyEnv());
    }

    public Scan open() {
        return new ProjectionScan(plan.open(), projections, env);
    }

    public void addFunctionsToCalc(Set<String> funcs){
    	funcSet.addAll(funcs);
        plan.addFunctionsToCalc(funcs);
    }

}