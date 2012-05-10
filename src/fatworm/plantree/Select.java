package fatworm.plantree;

import java.util.LinkedList;
import java.util.Set;

import fatworm.absyn.AndList;
import fatworm.absyn.BoolExpr;

public class Select extends Node{
	public BoolExpr boolValue;
	public Select(BoolExpr boolValue){
		this.boolValue = boolValue;
	}
	public String toString() {
		return "Select\t"+boolValue.toString();
	}
	
	public Set<String> dumpUsefulColumns(){
		Set<String> result = super.dumpUsefulColumns();
		result.addAll(boolValue.dumpUsefulColumns());
		return result;
	}
		
}
