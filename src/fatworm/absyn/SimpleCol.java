package fatworm.absyn;

import org.antlr.runtime.tree.CommonTree;

public class SimpleCol extends ColName{
	String id;
	public SimpleCol(CommonTree t){
		super(t);
		id = t.getChild(0).getText();
	}
	public String toString(){
		return id;
	}
}
