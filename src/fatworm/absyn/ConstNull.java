package fatworm.absyn;


import fatworm.dataentity.DataEntity;
import fatworm.dataentity.NullDataEntity;
import fatworm.query.Env;

public class ConstNull extends ConstValue{
	public ConstNull(String val){
        super(val);
	}
    public DataEntity getValue(Env env) {
        return new NullDataEntity();
    }
} 
