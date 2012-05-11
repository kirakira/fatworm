package fatworm.absyn;

import java.math.BigDecimal;
import java.util.Map;

import fatworm.dataentity.DataEntity;
import fatworm.dataentity.Decimal;
import fatworm.query.Env;

public class ConstFloat extends ConstValue{
	public ConstFloat(String val){
        super(val);
	}
    public DataEntity getValue(Env env){
        return new Decimal(new BigDecimal(val));
    }
    @Override
	public int getType(Map<String, Integer> typemap) {
		return java.sql.Types.FLOAT;
	}
} 
