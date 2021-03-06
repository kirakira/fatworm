package fatworm.query;

import java.util.List;

import fatworm.absyn.OrderByColumn;
import fatworm.dataentity.DataEntity;



abstract public class OrderContainer {

	int[] orderKeyColNum;
	String[] extraOrderKeyColName;
	boolean[] orderKeyColDesc;
	int extraOrderKeyNumber = 0;
	int resultColumnNumber;
	Scan scan;
	public OrderContainer(Scan scan, List<OrderByColumn> order) {
		this.scan = scan;		
		int i = 0;
		resultColumnNumber = scan.getNumberOfColumns();
		orderKeyColNum = new int[order.size()];
		extraOrderKeyColName = new String[order.size()];
		orderKeyColDesc = new boolean[order.size()];
		for (OrderByColumn orderkey: order) {
			if (scan.hasColumn(orderkey.col.toString())) {
				orderKeyColNum[i] = scan.indexOfColumn(orderkey.col.toString());
			}
			else {
				orderKeyColNum[i] = resultColumnNumber + extraOrderKeyNumber;
				extraOrderKeyColName[extraOrderKeyNumber++] = orderkey.col.toString();
			}
			orderKeyColDesc[i] = orderkey.desc;
			i++;
		}
	}
	
	abstract public void sort();
	abstract public DataEntity getColumnByIndex(int index);
	abstract public boolean next();
	abstract public void beforeFirst();
}
