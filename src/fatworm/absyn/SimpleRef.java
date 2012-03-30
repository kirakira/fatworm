package fatworm.absyn;

import org.antlr.runtime.tree.CommonTree;

public class SimpleRef extends TableRef{
	String id;
	public SimpleRef(CommonTree t){
		id = t.getText();
	}
}
