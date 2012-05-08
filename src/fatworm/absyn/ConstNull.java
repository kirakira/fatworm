package fatworm.absyn;


import fatworm.dataentity.DataEntity;
import fatworm.query.Env;

public class ConstNull extends ConstValue{
	public ConstNull(String val){
        super(val);
	}
    public DataEntity getValue(Env env) {
        return null;
    }
} 
