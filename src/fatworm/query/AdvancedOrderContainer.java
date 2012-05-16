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

    int limit = -1;

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

    private int length(DataEntity[] de) {
        int ret = 0;
        for (int i = 0; i < de.length; ++i)
            ret += de[i].estimatedSize() + 5;
        return ret + 4;
    }
	
	public void sort() {
        boolean fail = false;
        do {
            fail = false;

            tempTables = new ArrayList<RecordFile>();
            memory = true;

            int tupleSize;

            scan.beforeFirst();
            boolean end = false;
            do {
                int count = 0;
		        table = new ArrayList<DataEntity[]>();
                ArrayList<Object> occupy1 = new ArrayList<Object>();
                byte[] occupy2 = new byte[4096 * 10];
                int tsize;

                DataEntity[][] occupy3 = new DataEntity[4][];
                DataEntity[] ttuple = null;
                int occupy3_len = 0;

                tupleSize = 0;

                boolean read = false;
                try {
                    while (limit == -1 || count < limit) {
                        read = false;
                        occupy2 = null;
                        if (scan.next()) {
                            read = true;

                            ttuple = add(scan);
                            table.add(ttuple);

                            tsize = length(ttuple);
                            if (tsize > tupleSize)
                                tupleSize = tsize;

                            if (occupy3_len < occupy3.length) {
                                occupy3[occupy3_len] = ttuple;
                                ++occupy3_len;
                            }

                            occupy1.add(null);
                            ++count;
                            occupy2 = new byte[2 * tupleSize];
                        } else {
                            read = true;
                            end = true;
                            break;
                        }
                    }
                } catch (OutOfMemoryError e) {
                    if (read == false) {
                        fail = true;
                        table = null;
                        occupy1 = null;
                        occupy2 = null;
                        occupy3 = null;
                        System.out.println("Wan tuo le 1, count=" + count);
                        limit = count / 2;
                        e.printStackTrace();
                    }
                }

                if (fail)
                    break;

                occupy1 = null;
                occupy2 = null;
                occupy3 = null;
                //System.gc();
                try {
                    Collections.sort(table, comparator);

                    if (!end)
                        memory = false;

                    if (memory) {
		                iter = table.iterator();
                		current = null;
                        limit = count;
                    } else {
                        System.out.println("External sort");
                        RecordFile t = Storage.getInstance().insertTempTable(tupleSize);
                        tempTables.add(t);

                        for (DataEntity[] dea: table) {
                            t.insert(dea);
                        }
                        table = null;
                    }
                } catch (OutOfMemoryError e) {
                    table = null;
                    System.out.println("Wan tuo le 2, count=" + count);
                    e.printStackTrace();
                    fail = true;
                    limit = count / 2;
                }
            } while (!fail && !end);
        } while (fail);
	}

	private DataEntity[] add(Scan scan) {
		DataEntity[] tuple  = new DataEntity[resultColumnNumber + extraOrderKeyNumber];
		for (int i = 0; i < resultColumnNumber; i++) {
			tuple[i] = scan.getColumnByIndex(i);
		}
		for (int i = resultColumnNumber; i < resultColumnNumber + extraOrderKeyNumber; i++) {
			tuple[i] = scan.getOrderKey(extraOrderKeyColName[i - resultColumnNumber]);
		}
        return tuple;
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
            try {
            q = new PriorityQueue<TupleAndIndex>();
            for (int i = 0; i < tempTables.size(); ++i) {
                RecordFile rf = tempTables.get(i);
                rf.beforeFirst();
                if (rf.next())
                    q.add(new TupleAndIndex(rf.tuple(), i));
            }
            current = null;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }

}
