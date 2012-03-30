package fatworm.absyn;

import java.util.LinkedList;

public class OrList extends BoolExpr{
	public LinkedList<BoolExpr> orList = null;
	public OrList(LinkedList<BoolExpr> orList){
		this.orList = orList;
	}
}
