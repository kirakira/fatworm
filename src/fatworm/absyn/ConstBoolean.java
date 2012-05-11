package fatworm.absyn;

import java.util.Map;

import fatworm.dataentity.*;
import fatworm.query.Env;

public class ConstBoolean extends ConstValue{
	public ConstBoolean(String val){
        super(val);
	}
    public DataEntity getValue(Env env) {
        return new Bool(val.compareToIgnoreCase("TRUE")==0);
    }
    @Override
	public int getType(Map<String, Integer> typemap) {
		return java.sql.Types.BOOLEAN;
	}
}
