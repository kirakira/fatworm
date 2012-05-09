package fatworm.absyn;

import java.math.BigDecimal;
import fatworm.dataentity.DataEntity;
import fatworm.dataentity.Decimal;
import fatworm.query.Env;


public class ConstInt extends ConstValue{
	public ConstInt(String val){
        super(val);
	}

	public DataEntity getValue(Env env) {
		return new Decimal(new BigDecimal(val));
	}
} 
