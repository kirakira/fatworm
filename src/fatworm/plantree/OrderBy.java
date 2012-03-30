package fatworm.plantree;

import java.util.LinkedList;

import fatworm.absyn.ColName;

public class OrderBy extends Node{
	LinkedList<ColName> colNameList;
	public OrderBy(LinkedList<ColName> colNameList){
		this.colNameList = colNameList;
	}
}
