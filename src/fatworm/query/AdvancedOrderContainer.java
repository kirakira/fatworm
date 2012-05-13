package fatworm.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import fatworm.absyn.OrderByColumn;
import fatworm.dataentity.DataEntity;
import fatworm.record.RecordFile;
import fatworm.record.Schema;
import fatworm.storage.Storage;

public class AdvancedOrderContainer extends OrderContainer {

	List<DataEntity[]> table;
	Iterator<DataEntity[]> iter;
	DataEntity[] current;
	Comparator<DataEntity[]> comparator;

    boolean memory;
    ArrayList<RecordFile> tempTables;
    PriorityQueue<TupleAndIndex> q;

    private class TupleAndIndex implements Comparable<TupleAndIndex> {
        DataEntity[] tuple;
        int index;

        public TupleAndIndex(DataEntity[] tuple, int index) {
            this.tuple = tuple;
            this.index = index;
        }

        public int compareTo(TupleAndIndex o) {
            return comparator.compare(tuple, o.tuple);
        }
    }

	public AdvancedOrderContainer(Scan scan, List<OrderByColumn> order) {
		super(scan, order);
		table = new ArrayList<DataEntity[]>();
		comparator = new TupleComparator(orderKeyColNum, orderKeyColDesc);
        memory = true;
	}
	
	public void sort() {
        int limit = -1;
        boolean fail = false;
        do {
            fail = false;

            tempTables = new ArrayList<RecordFile>();
            memory = true;

            scan.beforeFirst();
            boolean end = false;
            do {
                int count = 0;
		        table = new ArrayList<DataEntity[]>();
                ArrayList<Object> occupy1 = new ArrayList<Object>();
                byte[] occupy2 = new byte[4096 * 10];

                try {
                    while (limit == -1 || count < limit) {
                        if (scan.next()) {
                            add(scan);
                            occupy1.add(null);
                            ++count;
                        } else {
                            end = true;
                            break;
                        }
                    }
                } catch (OutOfMemoryError e) {
                }

                occupy1 = null;
                occupy2 = null;
                //System.gc();
                try {
                    Collections.sort(table, comparator);

                    if (!end)
                        memory = false;

                    if (memory) {
		                iter = table.iterator();
                		current = null;
                    } else {
                        RecordFile t = Storage.getInstance().insertTempTable();
                        tempTables.add(t);

                        for (DataEntity[] dea: table) {
                            t.insert(dea);
                        }
                        table = null;
                    }
                } catch (OutOfMemoryError e) {
                    table = null;
                    System.out.println("Wan tuo le");
                    fail = true;
                    limit = count / 2;
                }
            } while (!fail && !end);
        } while (fail);
	}

	private void add(Scan scan) {
		DataEntity[] tuple  = new DataEntity[resultColumnNumber + extraOrderKeyNumber];
		for (int i = 0; i < resultColumnNumber; i++) {
			tuple[i] = scan.getColumnByIndex(i);
		}
		for (int i = resultColumnNumber; i < resultColumnNumber + extraOrderKeyNumber; i++) {
			tuple[i] = scan.getOrderKey(extraOrderKeyColName[i - resultColumnNumber]);
		}
		table.add(tuple);
	}

	@Override
	public DataEntity getColumnByIndex(int index) {
        return current[index];
	}

	@Override
	public boolean next() {
        if (memory) {
    		if (iter.hasNext()) {
	    		current = iter.next();
		    	return true;
    		}
	    	return false;
        } else {
            if (q.isEmpty())
                return false;
            else {
                TupleAndIndex top = q.poll();
                current = top.tuple;
                int i = top.index;
                if (tempTables.get(i).next())
                    q.add(new TupleAndIndex(tempTables.get(i).tuple(), new Integer(i)));
                return true;
            }
        }
	}

	@Override
	public void beforeFirst() {
        if (memory) {
    		iter = table.iterator();
	    	current = null;
        } else {
            q = new PriorityQueue<TupleAndIndex>();
            for (int i = 0; i < tempTables.size(); ++i) {
                RecordFile rf = tempTables.get(i);
                rf.beforeFirst();
                if (rf.next())
                    q.add(new TupleAndIndex(rf.tuple(), i));
            }
            current = null;
        }
    }

}
