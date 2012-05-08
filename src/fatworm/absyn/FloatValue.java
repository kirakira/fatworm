package fatworm.absyn;

import fatworm.dataentity.DataEntity;
import fatworm.query.Env;

public class FloatValue extends Value{
	public Double fl;
	public FloatValue(Double fl){
		this.fl = fl;
	}
	public String toString(){
		return fl.toString();
	}
	@Override
	public DataEntity getValue(Env env) {
		// TODO Auto-generated method stub
		return null;
	}
}
