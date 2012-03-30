package fatworm.absyn;

import fatworm.plantree.Node;

public class CompareAnyExpr extends BoolExpr{
	Value val;
	Node query;
	String cop;
	public CompareAnyExpr(Value val, Node query, String cop){
		this.val = val;
		this.query = query;
		this.cop = cop;
	}
}
