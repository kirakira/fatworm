package fatworm.query;

import java.util.Set;

import fatworm.dataentity.DataEntity;
import fatworm.functioncalculator.FunctionCalculator;
import fatworm.util.Util;

public abstract class GroupContainer {
	String key;
	String[] funcNameList;
	String[] variableList;
	FunctionCalculator[] calculator;
	public GroupContainer(String key, Set<String> funcNameSet) {
		this.key = key;
		Object[] tmp = funcNameSet.toArray();
		funcNameList = new String[tmp.length];
		for (int i =0; i < tmp.length; i++)
			this.funcNameList[i] = (String)tmp[i];
		calculator = new FunctionCalculator[funcNameList.length];
		variableList = new String[funcNameList.length];
		for(int i = 0; i < funcNameList.length; i++) { 
			calculator[i] = FunctionCalculator.getFunctionCalculator(funcNameList[i]);
			variableList[i] = Util.getFuncVariable(funcNameList[i]);
		}
	}
	
	abstract public void update(Scan scan);
	abstract public void finish();
	abstract public DataEntity getFunctionValue(String func);
	abstract public DataEntity getKeyValue();
	abstract public boolean next();
	abstract public void beforeFirst();
}
