package fatworm.absyn;

import org.antlr.runtime.tree.CommonTree;

public class ColumnDef {
	public String colName;
	public String dataType;
	public int type;
	public int length = 0;
	public boolean isNull = false,isNotNull = false;
	public boolean autoIncrement = false;
	public boolean primary = false;
	public ConstValue defaultValue = null;
	
	public ColumnDef(CommonTree tree){
		this.colName = tree.getChild(0).getText();
		this.dataType = tree.getChild(1).getText();
		this.setLength(tree);
	}
	public void setLength(CommonTree tree){
		this.init();
		if (tree.getChild(1).getText().indexOf("DECIMALVARCHAR") != -1){
			length += Integer.parseInt(tree.getChild(1).getChild(0).getText());
		}
		if (tree.getChild(1).getText().startsWith("DECIMAL") && tree.getChildCount() > 2){
			length += Integer.parseInt(tree.getChild(2).getChild(0).getText());
		}
	}
	public void setIsNull(){
		isNull = true;
	}
	public void setIsNotNull(){
		isNotNull = true;
	}
	public void setDefaultValue(ConstValue d){
		this.defaultValue = d;
	}
	public void setAutoIncrement(){
		autoIncrement = true;
	}
	public void init(){
		if (dataType.startsWith("INT")){
			length = 4;
			type = java.sql.Types.INTEGER;
		}
		if (dataType.startsWith("FLOAT")){
			length = 8;
			type = java.sql.Types.FLOAT;
		}
		if (dataType.startsWith("DATETIME")){
			length = 8;
			type = java.sql.Types.DATE;
		}
		if (dataType.startsWith("TIMESTAMP")){
			length = 8;
			type = java.sql.Types.TIMESTAMP;
		}
		if (dataType.startsWith("BOOLEAN")){
			length = 1;
			type = java.sql.Types.BOOLEAN;
		}
		if (dataType.startsWith("VARCHAR")){
			type = java.sql.Types.VARCHAR;
		}
		if (dataType.startsWith("CHAR")){
			type = java.sql.Types.CHAR;
		}
		if (dataType.startsWith("DECIMAL")){
			type = java.sql.Types.DECIMAL;
		}
	}
}
