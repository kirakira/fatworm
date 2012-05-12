package fatworm.query;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import fatworm.dataentity.DataEntity;
import fatworm.functioncalculator.FuncValue;

public class MemoryGroupContainer extends GroupContainer {

	
	Map<DataEntity, FuncValue[]> funcValues;
	LinkedList<DataEntity[]> resultValues;
	DataEntity[] current;
	Iterator<DataEntity[]> iter;
	
	public MemoryGroupContainer(String key, Set<String> funcNameSet) {
		super(key, funcNameSet);
		funcValues = new HashMap<DataEntity, FuncValue[]>();
	}
	
	@Override
	public void update(Scan scan) {
		DataEntity pointer = scan.getColumn(key);
		if (pointer.isNull())
			return;
		FuncValue[] result = funcValues.get(pointer);
		if (result == null) {
			result = new FuncValue[funcNameList.length];
			for (int i = 0; i < result.length; i++)
				result[i] = new FuncValue();
			funcValues.put(pointer, result);
		}
		for (int i = 0; i < result.length; i++) {
			calculator[i].update(result[i], scan.getColumn(variableList[i]));
		} 
	}

	public void finish() {
		resultValues = new LinkedList<DataEntity[]>();
		for (DataEntity key: funcValues.keySet()) {
			FuncValue[] raw = funcValues.get(key);
			DataEntity[] result = new DataEntity[raw.length + 1];
			for (int i = 0; i < raw.length; i++)
				result[i] = calculator[i].getResult(raw[i]);
			result[result.length - 1] = key;
			resultValues.add(result);
		}
	}
	@Override
	public DataEntity getFunctionValue(String func) {
		for (int i = 0; i < funcNameList.length; i++) {
			if (funcNameList[i].equals(func))
				return current[i];
		}
		return null;
	}

	@Override
	public DataEntity getKeyValue() {
		return current[current.length - 1];
	}

	@Override
	public boolean next() {
		if (iter.hasNext()) {
			current = iter.next();
			return true;
		}
		return false;
	}

	@Override
	public void beforeFirst() {
		iter = resultValues.iterator();
		current = null;
	}
}
