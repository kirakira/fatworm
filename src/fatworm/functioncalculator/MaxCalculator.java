package fatworm.functioncalculator;

import fatworm.dataentity.DataEntity;
import fatworm.dataentity.NullDataEntity;

public class MaxCalculator extends FunctionCalculator {

	@Override
	public void update(FuncValue oldValue, DataEntity nextValue) {
		if (nextValue.isNull())
			return;
		if (oldValue.value.isNull()) 
			oldValue.value = nextValue;
		else if (oldValue.value.compareTo(nextValue) < 0)
			oldValue.value = nextValue;
		oldValue.count++;
	}

	@Override
	public DataEntity getResult(FuncValue value) {
		if (value.count > 0)
			return value.value;
		return new NullDataEntity();
	}

}
