package fatworm.absyn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fatworm.dataentity.DataEntity;
import fatworm.query.Env;

public class FuncValue extends Value{
	String func;
	ColumnValue val;
	public FuncValue(String f, Value val){
		this.func = f.toUpperCase();
		this.func = func.substring(0, func.indexOf('('));
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
	@Override
	public int getType(Map<String, Integer> typemap) {
		return typemap.get(toString());
	}
	
}
