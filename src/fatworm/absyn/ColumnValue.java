package fatworm.absyn;

public class ColumnValue extends Value{
	ColName colName;
	public ColumnValue(ColName colName){
		this.colName = colName;
	}
	public String toString(){
		return colName.toString();
	}
}
