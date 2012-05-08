package fatworm.absyn;

import org.antlr.runtime.tree.CommonTree;

public class ColName {
	public ColName(CommonTree t){
		
	}
	public ColName getColName(CommonTree t){
		if (t.getText().startsWith("SimpleColumn")){
			return new SimpleCol(t);
		}
		if (t.getText().startsWith("FieldColumn")){
			return new FieldCol(t);
		}
		return null;
	}
	public void print(){
		System.out.println("mustn't be echoed, something wrong@ColName");
	}
}
