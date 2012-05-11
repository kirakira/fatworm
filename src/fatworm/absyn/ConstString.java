package fatworm.absyn;


import java.util.Map;

import fatworm.dataentity.DataEntity;
import fatworm.dataentity.VarChar;
import fatworm.query.Env;

public class ConstString extends ConstValue{
	public ConstString(String val){
        super(val);
	}
    public DataEntity getValue(Env env){
        return new VarChar(val);
    }
    @Override
	public int getType(Map<String, Integer> typemap) {
		return java.sql.Types.VARCHAR;
	}
} 
