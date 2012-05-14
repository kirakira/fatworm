package fatworm.plantree;

public class Table extends Node{
	public String name;
	public Table(String name){
		this.name = name.toLowerCase();
	}
	public String toString(){
		return "Table\t"+name;
	}
}
