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
	@Override
	public DataEntity getValue(Env env) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
