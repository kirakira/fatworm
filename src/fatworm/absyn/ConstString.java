package fatworm.absyn;


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
} 
