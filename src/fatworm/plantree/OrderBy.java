package fatworm.plantree;

import java.util.LinkedList;

import fatworm.absyn.ColName;

public class OrderBy extends Node{
	LinkedList<ColName> colNameList;
	public OrderBy(LinkedList<ColName> colNameList){
		this.colNameList = colNameList;
	}
	public String toString(){
		String colNames = "OrderBy";
		for (ColName name: colNameList){
			colNames = colNames + "\t"+name.toString();
		}
		return colNames;
	}
}
