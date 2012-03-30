package fatworm.absyn;

import fatworm.plantree.Node;

public class ExistExpr extends BoolExpr{
	boolean empty; // true for "NotExist" false for "Exist"
	Node query;
	public ExistExpr(boolean empty, Node query){
		this.empty = empty;
		this.query = query;
	}
}
