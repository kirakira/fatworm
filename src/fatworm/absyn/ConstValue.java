package fatworm.absyn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fatworm.dataentity.DataEntity;
import fatworm.query.Env;

public class ConstValue extends Value{
	public String val;
	public ConstValue(String val){
		this.val = val;
	}

	public String toString(){
		return val;
	}
	@Override
	public DataEntity getValue(Env env) {
		return null;
	}
	
	
    public Set<String> dumpUsefulColumns() {
    	Set<String> result = new HashSet<String>();
    	return result;
    }

	@Override
	public int getType(Map<String, Integer> typemap) {
		return 0;
	}
	
}
