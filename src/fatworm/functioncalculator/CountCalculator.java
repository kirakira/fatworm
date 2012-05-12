package fatworm.functioncalculator;

import fatworm.dataentity.DataEntity;
import fatworm.dataentity.Int;

public class CountCalculator extends FunctionCalculator {
	@Override
	public void update(FuncValue oldValue, DataEntity nextValue) {
		if (nextValue.isNull())
			return;
		oldValue.count++;
	}

	@Override
	public DataEntity getResult(FuncValue value) {
		return new Int(value.count);
	}
}
