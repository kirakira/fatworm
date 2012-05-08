package fatworm.plantree;

public class Rename extends Node{
	String alias;
	public Rename(String alias){
		this.alias = alias;
	}
	public String toString(){
		return "Rename\t"+alias;
	}
}
