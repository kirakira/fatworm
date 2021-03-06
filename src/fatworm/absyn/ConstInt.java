package fatworm.absyn;

import java.math.BigDecimal;
import java.util.Map;

import fatworm.dataentity.DataEntity;
import fatworm.dataentity.Decimal;
import fatworm.dataentity.Int;
import fatworm.query.Env;


public class ConstInt extends ConstValue{
	public ConstInt(String val){
        super(val);
	}

	public DataEntity getValue(Env env) {
		try {
			return new Int(Integer.valueOf(val).intValue());
		} catch (Exception e) {
			return new Decimal(new BigDecimal(val));
		}
	}
	
	@Override
	public int getType(Map<String, Integer> typemap) {
		return java.sql.Types.INTEGER;
	}
} 
