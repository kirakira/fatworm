package fatworm.plantree;

import java.util.LinkedList;

import fatworm.absyn.SelectExprList;
import fatworm.absyn.Value;

public class Projection extends Node{
	LinkedList<Value> valList;
	public Projection(LinkedList<Value> valList){
		this.valList = valList;
	}
	public void print() {
		System.out.println( "(Project)");
	}
}
