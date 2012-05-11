package fatworm.absyn;


import java.util.Map;

import fatworm.dataentity.DataEntity;
import fatworm.dataentity.TimeStamp;
import fatworm.query.Env;


public class ConstTimeStamp extends ConstValue{
	public ConstTimeStamp(String val){
        super(val);
	}
    public DataEntity getValue(Env env){
        return new TimeStamp(java.sql.Timestamp.valueOf(val));
    }
    @Override
	public int getType(Map<String, Integer> typemap) {
		return java.sql.Types.TIMESTAMP;
	}
} 
