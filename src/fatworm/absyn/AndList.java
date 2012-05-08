package fatworm.absyn;

import java.util.LinkedList;

public class AndList extends BoolExpr{
	public LinkedList<BoolExpr> andList = null;
	public AndList(LinkedList<BoolExpr> andList){
		this.andList = andList;
	}
	public String toString(){
		String ands = "";
		for (BoolExpr expr: andList){
			if (ands != "") ands += "&";
			ands += expr.toString();
		}
		return ands;
	}
}
