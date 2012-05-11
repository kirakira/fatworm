package fatworm.absyn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fatworm.dataentity.DataEntity;
import fatworm.query.Env;
import fatworm.util.Util;
abstract public class Value {

    abstract public DataEntity getValue(Env env);
    abstract public Set<String> dumpUsefulColumns();
    
    public Set<String> dumpUsefulFunctions() {
        Set<String> candidate = dumpUsefulColumns();
        Set<String> result = new HashSet<String>();
        for(String column : candidate) {
        	if (Util.isFunction(column))
        		result.add(column);
        }
        return result;
    }
	abstract public int getType(Map<String, Integer> typemap);

}
