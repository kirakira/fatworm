package fatworm.functioncalculator;

import fatworm.dataentity.DataEntity;

abstract public class FunctionCalculator {

	public static FunctionCalculator avg = new AvgCalculator();
	public static FunctionCalculator count = new CountCalculator();
	public static FunctionCalculator sum = new SumCalculator();
	public static FunctionCalculator max = new MaxCalculator();
	public static FunctionCalculator min = new MinCalculator();
	
	public static FunctionCalculator getFunctionCalculator(String func) {
		func = func.toUpperCase();
		if (func.startsWith("AVG"))
			return avg;
		else if (func.startsWith("COUNT"))
			return count;
		else if (func.startsWith("MAX"))
			return max;
		else if (func.startsWith("MIN"))
			return min;
		else if (func.startsWith("SUM"))
			return sum;
		return null;
	}
	
	abstract public void update(FuncValue oldValue, DataEntity nextValue);
	abstract public DataEntity getResult(FuncValue value);
}
