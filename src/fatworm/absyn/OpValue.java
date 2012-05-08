package fatworm.absyn;

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
}
