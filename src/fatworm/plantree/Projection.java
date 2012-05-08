package fatworm.plantree;

import java.util.LinkedList;

import fatworm.absyn.ProjectionValue;
import fatworm.absyn.SelectExprList;
import fatworm.absyn.Value;

public class Projection extends Node{
	LinkedList<ProjectionValue> valList;
	public Projection(LinkedList<ProjectionValue> valList){
		this.valList = valList;
	}
	public String toString() {
		String values = "Projection";
		for (ProjectionValue v : valList){
			values = values+"\t"+v.toString();
		}
		return values;
	}
}
