package fatworm.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import fatworm.dataentity.DataEntity;


public class MemoryDistinctContainer extends DistinctContainer {

	List<DataEntity[]> table;
	Iterator<DataEntity[]> iter;
	DataEntity[] current;
	Map<Integer, List<Integer>> map;

	public MemoryDistinctContainer() {
		table = new ArrayList<DataEntity[]>();
		map = new HashMap<Integer, List<Integer>>();
	}
	@Override
	public void update(Scan scan) {
		DataEntity[] tuple = getTuple(scan);
		Integer hashcode = new Integer(arrayHashCode(tuple));
		List<Integer> bucket = map.get(hashcode); 
		if (bucket == null) {
			bucket = new ArrayList<Integer>();
			map.put(hashcode, bucket);
		}
		for (Integer pointer: bucket) {
			if (tupleEqual(table.get(pointer.intValue()), tuple))
				return;
		}
		bucket.add(table.size());
		table.add(tuple);
	}

	boolean tupleEqual(DataEntity[] t1, DataEntity[] t2) {
		for (int i = 0; i < t1.length; i ++) {
			if (!t1[i].equals(t2[i]))
				return false;
		}
		return true;
	}
	DataEntity[] getTuple(Scan scan) {
		DataEntity[] tuple = new DataEntity[scan.getNumberOfColumns()];
		for (int i = 0; i < tuple.length; i ++) {
			tuple[i] = scan.getColumnByIndex(i);
		}
		return tuple;
	}
	
	int arrayHashCode(DataEntity[] tuple) {
		int result = 0;
		for (int i =0; i < tuple.length; i++) {
			result ^= tuple[i].hashCode(); 
		}
		return result;
	}
	@Override
	public void finish() {

	}

	@Override
	public DataEntity getColumnByIndex(int index) {
		return current[index];
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
		iter = table.iterator();
		current = null;
	}
}
