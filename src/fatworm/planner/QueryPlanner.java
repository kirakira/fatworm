package fatworm.planner;


import fatworm.plantree.Node;
import fatworm.query.Env;
import fatworm.query.QueryPlan;

/**
 * The interface implemented by planners for 
 * the SQL select statement.
 *
 */
public interface QueryPlanner {
   
   /**
    * Creates a plan for the parsed query.
    * @param query the logicplan of a query 
    * @return a physical plan for that query
    */
    public QueryPlan createQueryPlan(Node query);
    public QueryPlan createQueryPlan(Node query, Env env);
}
