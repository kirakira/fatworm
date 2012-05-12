package fatworm.absyn;

import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.tree.CommonTree;

public class ColName {
	public ColName(CommonTree t){
		
	}
	public static ColName getColName(CommonTree t){
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
	
	
    public Set<String> dumpUsefulColumn() {
    	Set<String> result = new HashSet<String>();
    	result.add(this.toString());
    	return result;
    }

}
