package fatworm.absyn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fatworm.dataentity.DataEntity;
import fatworm.query.Env;

public class OpValue extends Value{
	public String op;
	public Value left, right;
	public OpValue(String op, Value left, Value right){
		this.op = op;
		this.left = left;
		this.right = right;
	}

	public String toString(){
		return left.toString()+op+right.toString();
	}

	@Override
	public DataEntity getValue(Env env) {
		return  left.getValue(env).opWith(right.getValue(env),op);
	}
	
	public Set<String> dumpUsefulColumns() {
    	Set<String> result = new HashSet<String>();
    	result.addAll(left.dumpUsefulColumns());
    	result.addAll(right.dumpUsefulColumns());
    	return result;
    }

	@Override
	public int getType(Map<String, Integer> typemap) {
		int l = left.getType(typemap);
		int r= right.getType(typemap);
		if (op.equals("/"))
			return java.sql.Types.FLOAT;
		if (l == r)
			return l;
		else 
			return java.sql.Types.FLOAT;
		
	}
}
