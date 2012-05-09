package fatworm.plantree;

import java.util.LinkedList;

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
}
