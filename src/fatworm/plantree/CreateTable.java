package fatworm.plantree;

import java.util.LinkedList;

import fatworm.absyn.ColumnDef;

public class CreateTable extends Node {
	String tableName;
	LinkedList<ColumnDef> columnDefList = new LinkedList<ColumnDef>();
	LinkedList<String> primaryKeyList = new LinkedList<String>();
	public CreateTable(String tableName, LinkedList<ColumnDef> columnDefList, LinkedList<String> primaryKeyList){
		this.tableName = tableName;
		this.columnDefList = columnDefList;
		this.primaryKeyList = primaryKeyList;
	}
}
