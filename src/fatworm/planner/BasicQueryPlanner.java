package fatworm.planner;

import java.util.ArrayList;
import java.util.List;

import fatworm.plantree.Join;
import fatworm.plantree.Node;
import fatworm.plantree.GroupBy;
import fatworm.plantree.Projection;
import fatworm.plantree.Rename;
import fatworm.plantree.Select;
import fatworm.plantree.Table;
import fatworm.plantree.OrderBy;
import fatworm.query.Env;
import fatworm.query.GroupPlan;
import fatworm.query.JoinPlan;
import fatworm.query.ProjectionPlan;
import fatworm.query.QueryPlan;
import fatworm.query.RenamePlan;
import fatworm.query.SelectPlan;
import fatworm.query.TablePlan;
import fatworm.query.OrderPlan;

public class BasicQueryPlanner implements QueryPlanner {

	@Override
	public QueryPlan createQueryPlan(Node query) {
		if (query instanceof OrderBy) {
			return new OrderPlan(createQueryPlan(query.childList.getFirst()), ((OrderBy)query).colNameList);
		}
		else if (query instanceof Projection) {
			return new ProjectionPlan(createQueryPlan(query.childList.getFirst()), ((Projection)query).valList);
		}
		else if (query instanceof Select) {
			return new SelectPlan(createQueryPlan(query.childList.getFirst()), ((Select) query).boolValue);
		}
		else if (query instanceof GroupBy) {
			return new GroupPlan(createQueryPlan(query.childList.getFirst()), ((GroupBy) query).colName.toString());
		}
		else if (query instanceof Join) {
			List<QueryPlan>  joinlist = new ArrayList<QueryPlan>();
			for (Node node: query.childList) {
				joinlist.add(createQueryPlan(node));
			}
			return new JoinPlan(joinlist);
		}
		else if (query instanceof Rename) {
			return new RenamePlan(createQueryPlan(query.childList.getFirst()), ((Rename)query).alias);
		} 
		else if (query instanceof Table) {
			return new TablePlan(((Table) query).name);
		}
		return null;
	}

	@Override
	public QueryPlan createQueryPlan(Node query, Env env) {
		if (query instanceof OrderBy) {
			return new OrderPlan(createQueryPlan(query.childList.getFirst(), env), ((OrderBy)query).colNameList);
		}		
		if (query instanceof Projection) {
			return new ProjectionPlan(createQueryPlan(query.childList.getFirst(), env), ((Projection)query).valList, env);
		}
		else if (query instanceof Select) {
			return new SelectPlan(createQueryPlan(query.childList.getFirst(), env), ((Select) query).boolValue, env);
		}
		else if (query instanceof GroupBy) {
			return new GroupPlan(createQueryPlan(query.childList.getFirst(), env), ((GroupBy) query).colName.toString());
		}		
		else if (query instanceof Join) {
			List<QueryPlan>  joinlist = new ArrayList<QueryPlan>();
			for (Node node: query.childList) {
				joinlist.add(createQueryPlan(node, env));
			}
			return new JoinPlan(joinlist);
		}
		else if (query instanceof Rename) {
			return new RenamePlan(createQueryPlan(query.childList.getFirst(), env), ((Rename)query).alias);
		} 
		else if (query instanceof Table) {
			return new TablePlan(((Table) query).name);
		}
		return null;
	}

}
