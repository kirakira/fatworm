package fatworm.absyn;

import org.antlr.runtime.tree.CommonTree;

public class FieldCol extends ColName{
	private String table,col;
	public FieldCol(CommonTree t){
		super(t);
		table = t.getChild(0).getText().toLowerCase();
		col = t.getChild(1).getText().toLowerCase();
	}
	public String toString(){
		return table+"."+col;
	}
}
