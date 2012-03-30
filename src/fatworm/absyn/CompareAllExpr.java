package fatworm.absyn;

import fatworm.plantree.Node;

public class CompareAllExpr extends BoolExpr{
	Value val;
	Node query;
	String cop;
	public CompareAllExpr(Value val, Node query, String cop){
		this.val = val;
		this.query = query;
		this.cop = cop;
	}
}
