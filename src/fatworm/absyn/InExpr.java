package fatworm.absyn;

import fatworm.plantree.Node;

public class InExpr extends BoolExpr{
	Value val;
	Node query;
	public InExpr(Value val, Node query){
		this.val = val;
		this.query = query;
	}
}
