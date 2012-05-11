package fatworm.plantree;

import java.util.LinkedList;

import fatworm.absyn.ColName;
import fatworm.absyn.Value;

public class InsertStmt extends Node {
	public String tableName;
	public LinkedList<Value> valueList = null;
	public LinkedList<ColName> colNameList = null;
	public Node query = null;
	
	public InsertStmt(String tableName, LinkedList<Value> valueList, LinkedList<ColName> colNameList, Node query){
		this.tableName = tableName;
		this.valueList = valueList;
		this.colNameList = colNameList;
		this.query = query;
	}
}
