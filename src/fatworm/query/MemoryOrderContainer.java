package fatworm.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import fatworm.absyn.OrderByColumn;
import fatworm.dataentity.DataEntity;

public class MemoryOrderContainer extends OrderContainer {

	List<DataEntity[]> table;
	Iterator<DataEntity[]> iter;
	DataEntity[] current;
	Comparator<DataEntity[]> comparator;
	
	public MemoryOrderContainer(Scan scan, List<OrderByColumn> order) {
		super(scan, order);
		table = new ArrayList<DataEntity[]>();
		comparator = new TupleComparator(orderKeyColNum, orderKeyColDesc);
	}
	
	public void sort() {
		scan.beforeFirst();
		while(scan.next()) {
			update(scan);
		}
		finish();
	}

	private void update(Scan scan) {
		DataEntity[] tuple  = new DataEntity[resultColumnNumber + extraOrderKeyNumber];
		for (int i = 0; i < resultColumnNumber; i++) {
			tuple[i] = scan.getColumnByIndex(i);
		}
		for (int i = resultColumnNumber; i < resultColumnNumber + extraOrderKeyNumber; i++) {
			tuple[i] = scan.getOrderKey(extraOrderKeyColName[i - resultColumnNumber]);
		}
		table.add(tuple);
	}

	private void finish() {
		Collections.sort(table, comparator);
		iter = table.iterator();
		current = null;
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
