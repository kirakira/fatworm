package fatworm.absyn;

import java.util.Map;
import java.util.Set;

import fatworm.dataentity.DataEntity;
import fatworm.query.Env;

public class ProjectionValue {
	Value val;
	ProjectionValue(Value val){
		this.val = val;
	}
	
	public Set<String> dumpUsefulColumns(){
		return val.dumpUsefulColumns();
	}
	
	public Set<String> dumpUsefulFunctions() {
		return val.dumpUsefulFunctions();
	}

	public String toString() {
		return val.toString();
	}
	
	public DataEntity getValue(Env env) {
		return val.getValue(env);
	}

	public int getType(Map<String, Integer> typemap) {
		return val.getType(typemap);
	}
}
