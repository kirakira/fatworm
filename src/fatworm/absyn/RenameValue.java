package fatworm.absyn;

import fatworm.dataentity.DataEntity;
import fatworm.query.Env;

public class RenameValue {
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
