package fatworm.plantree;

import java.util.LinkedList;

import fatworm.absyn.SelectExprList;
import fatworm.absyn.Value;

public class Projection extends Node{
	LinkedList<Value> valList;
	public Projection(LinkedList<Value> valList){
		this.valList = valList;
	}
	public String toString() {
		String values = "Projection";
		for (Value v : valList){
			values = values+"\t"+v.toString();
		}
		return values;
	}
}
