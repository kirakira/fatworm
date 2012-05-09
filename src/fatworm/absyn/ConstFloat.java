package fatworm.absyn;

import java.math.BigDecimal;

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
} 
