package fatworm.plantree;

import java.util.LinkedList;
import java.util.Set;

import fatworm.absyn.ProjectionValue;

public class Projection extends Node{
	public LinkedList<ProjectionValue> valList;
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
	
	public Set<String> dumpUsefulColumns(){
		Set<String> result = super.dumpUsefulColumns();
		for (ProjectionValue val: valList)
			result.addAll(val.dumpUsefulColumns());
		return result;
	}
}
