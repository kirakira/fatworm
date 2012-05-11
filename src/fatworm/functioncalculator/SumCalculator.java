package fatworm.functioncalculator;

import fatworm.dataentity.DataEntity;
import fatworm.dataentity.NullDataEntity;

public class SumCalculator extends FunctionCalculator {
	@Override
	public void update(FuncValue oldValue, DataEntity nextValue) {
		if (nextValue.isNull())
			return;
		if (oldValue.value.isNull()) 
			oldValue.value = nextValue;
		else 
			oldValue.value = oldValue.value.opWith(nextValue, "+");
		oldValue.count++;
	}

	@Override
	public DataEntity getResult(FuncValue value) {
		if (value.count > 0)
			return value.value;
		return new NullDataEntity();
	}
}
