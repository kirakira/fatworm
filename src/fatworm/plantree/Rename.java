package fatworm.plantree;

public class Rename extends Node{
	public String alias;
	public Rename(String alias){
		this.alias = alias.toLowerCase();
	}
	public String toString(){
		return "Rename\t"+alias;
	}
}
