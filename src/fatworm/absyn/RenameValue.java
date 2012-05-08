package fatworm.absyn;

public class RenameValue extends Value{
	public Value val;
	public String alias;
	public RenameValue(Value val, String alias){
		this.val = val;
		this.alias = alias;
	}
	public String toString(){
		return "Rename "+val.toString()+" as "+alias;
	}
}
