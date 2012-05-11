package fatworm.functioncalculator;

import fatworm.dataentity.DataEntity;
import fatworm.dataentity.NullDataEntity;

public class FuncValue {
	DataEntity value = new NullDataEntity();
	int count = 0;	
	public DataEntity getResult() {
		return value;
	}
}
