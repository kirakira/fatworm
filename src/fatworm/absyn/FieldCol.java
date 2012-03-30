package fatworm.absyn;

import org.antlr.runtime.tree.CommonTree;

public class FieldCol extends ColName{
	String table,col;
	public FieldCol(CommonTree t){
		super(t);
		table = t.getChild(0).getText();
		col = t.getChild(1).getText();
	}
}
