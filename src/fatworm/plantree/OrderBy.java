package fatworm.plantree;

import java.util.LinkedList;

import fatworm.absyn.ColName;
import fatworm.absyn.OrderByColumn;

public class OrderBy extends Node{
	LinkedList<OrderByColumn> colNameList;
	public OrderBy(LinkedList<OrderByColumn> colNameList){
		this.colNameList = colNameList;
	}
	public String toString(){
		String colNames = "OrderBy";
		for (OrderByColumn name: colNameList){
			colNames = colNames + "\t"+name.toString();
		}
		return colNames;
	}
}
