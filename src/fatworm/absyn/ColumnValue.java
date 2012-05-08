package fatworm.absyn;

import fatworm.dataentity.DataEntity;
import fatworm.query.Env;

public class ColumnValue extends Value{
	ColName colName;
	public ColumnValue(ColName colName){
		this.colName = colName;
	}
	@Override
	public DataEntity getValue(Env env) {
		// TODO Auto-generated method stub
		return null;
	}
}
