package fatworm.plantree;

import java.util.LinkedList;

import fatworm.absyn.AndList;
import fatworm.absyn.BoolExpr;

public class Select extends Node{
	BoolExpr boolValue;
	public Select(BoolExpr boolValue){
		this.boolValue = boolValue;
	}
	public void print() {
		System.out.println( "(Select)");
	}
}
