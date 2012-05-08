package fatworm.absyn;

import fatworm.dataentity.DataEntity;
import fatworm.query.Env;

public class StringValue extends Value{
	public String s;
	public StringValue(String s){
		this.s = s;
	}
	public String toString(){
		return s;
	}
	@Override
	public DataEntity getValue(Env env) {
		// TODO Auto-generated method stub
		return null;
	}
}
