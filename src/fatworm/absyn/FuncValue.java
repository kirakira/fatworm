package fatworm.absyn;

import java.util.HashSet;
import java.util.Set;

import fatworm.dataentity.DataEntity;
import fatworm.query.Env;

public class FuncValue extends Value{
	String func;
	ColumnValue val;
	public FuncValue(String func, Value val){
		this.func = func.toUpperCase();
		this.val = (ColumnValue)val;
	}
	@Override
	public DataEntity getValue(Env env) {
        return env.getValue(this.toString());
	}
	public String toString(){
		return func+"("+val.toString()+")";
	}
	
    public Set<String> dumpUsefulColumns() {
    	Set<String> result = new HashSet<String>();
    	result.add(this.toString());
    	return result;
    }
	



}
