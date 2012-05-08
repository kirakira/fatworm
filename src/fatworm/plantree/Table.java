package fatworm.plantree;

public class Table extends Node{
	String name;
	public Table(String name){
		this.name = name;
	}
	public String toString(){
		return "Table\t"+name;
	}
}
