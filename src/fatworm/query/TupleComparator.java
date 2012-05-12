package fatworm.query;

import java.util.Comparator;

import fatworm.dataentity.DataEntity;

public class TupleComparator implements Comparator<DataEntity[]> {

	int [] orderKeyColNum;
	boolean[] orderKeyColDesc;
	
	public TupleComparator(int[] orderKeyColNum, boolean [] orderKeyColDesc) {
		this.orderKeyColNum = orderKeyColNum;
		this.orderKeyColDesc = orderKeyColDesc;
	}
	@Override
	public int compare(DataEntity[] arg0, DataEntity[] arg1) {
		for (int j = 0; j < orderKeyColNum.length; j++) {
			int i = orderKeyColNum[j];
			if (arg0[i].isNull() || arg1[i].isNull()) {
				if (arg0[i].isNull() && arg1[i].isNull())
					continue;
				else {
					int result = (arg0[i].isNull()? -1: 1);
					if (orderKeyColDesc[j])
						result = - result;
					return result;
				}
			}
			else {
				int result = arg0[i].compareTo(arg1[i]);
				if (result == 0)
					continue;
				else 
					return (orderKeyColDesc[j]? -result: result); 
			}
		}
		return 0;
	}

}
