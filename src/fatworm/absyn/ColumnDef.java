package fatworm.absyn;

import org.antlr.runtime.tree.CommonTree;

import fatworm.dataentity.DataEntity;

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
		this.dataType = tree.getChild(1).getText().toUpperCase();
		this.setLength(tree);
	}
	static String regx = "DecimalVarChar";
	public void setLength(CommonTree tree){
		this.init();
		//System.out.println(tree.getChild(1).getText());
		if (regx.indexOf(tree.getChild(1).getText()) != -1){
			//System.out.println(tree.getChild(1).getChild(0).getText());
			length += Integer.parseInt(tree.getChild(1).getChild(0).getText());
		}
		//System.out.println("----"+length);
		if (tree.getChild(1).getText().startsWith("Decimal") && tree.getChild(1).getChildCount() > 1){
			//System.out.println(tree.getChild(1).getChild(1).getText());
			length += Integer.parseInt(tree.getChild(1).getChild(1).getText());
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
			type = java.sql.Types.TIMESTAMP;
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
	public DataEntity getDefaultDataEntity(){
		if (defaultValue == null)
			return null;
		else 
			return defaultValue.getValue(null);
	}
	public String toString(){
		String ans ="ColumnDef\t"+colName+"\t"+dataType+"\t"+length;
		if (isNull) ans += "\tisNull";
		if (isNotNull) ans += "\tisNotNull";
		if (autoIncrement) ans += "\tautoInc";
		if (primary) ans+= "\tprimary";
		if (defaultValue!=null) ans+=("\tdefault="+defaultValue.toString());
		return ans;
	}
}
