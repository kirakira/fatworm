package fatworm.plantree;

import fatworm.absyn.ColName;

public class GroupBy extends Node{
	public ColName colName;
	public GroupBy(ColName colName){
		this.colName = colName;
	}
}
