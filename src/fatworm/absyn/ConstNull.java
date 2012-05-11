package fatworm.absyn;


import java.util.Map;

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
    
    @Override
	public int getType(Map<String, Integer> typemap) {
		return java.sql.Types.NULL;
	}
} 
