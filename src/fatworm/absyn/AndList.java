package fatworm.absyn;

import java.util.LinkedList;

public class AndList extends BoolExpr{
	public LinkedList<BoolExpr> andList = null;
	public AndList(LinkedList<BoolExpr> andList){
		this.andList = andList;
	}
}
