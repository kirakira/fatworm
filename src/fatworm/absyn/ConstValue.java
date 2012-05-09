package fatworm.absyn;

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
}
