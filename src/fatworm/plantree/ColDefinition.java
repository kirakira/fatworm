package fatworm.plantree;

import org.antlr.runtime.tree.CommonTree;

public class ColDefinition {
	String id;
	String type;
	ColDescription colDes;
	public ColDefinition(String id, String type, ColDescription colDes){
		this.id = id;
		this.type = type;
		this.colDes = colDes;
	}
	public static ColDefinition getColDefinition(CommonTree tree){
		return null;
	}
}
