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

    private int length(DataEntity[] de) {
        int ret = 0;
        for (int i = 0; i < de.length; ++i)
            ret += de[i].estimatedSize() + 5;
        return ret + 4;
    }

    private void printMemory() {
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.print("Memory: " + used / 1024 / 1024 + "MB");
        if (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE)
            System.out.println(" / No limit");
        else
            System.out.println(" / " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "MB");
    }
	
	public void sort() {
        int limit = -1;
        //System.out.println("Start sort");
        boolean fail = false;
        do {
            fail = false;

            tempTables = new ArrayList<RecordFile>();
            memory = true;

            int tupleSize;

            scan.beforeFirst();
            boolean end = false;
            do {
                //System.out.println("Start new run");
                int count = 0;
		        table = new ArrayList<DataEntity[]>();
                int tsize;

                DataEntity[] ttuple = null;

                tupleSize = 0;

                boolean read = false;
                try {
                    while (limit == -1 || count < limit) {
                        read = false;
                        if (scan.next()) {
                            /*
                            if (table.size() % 100000 == 0) {
                                System.out.println("Read 100 kilo new elements, count=" + table.size());
                                printMemory();
                            }*/
                            read = true;

                            ttuple = add(scan);
                            table.add(ttuple);

                            if (limit == -1 || tupleSize == 0) {
                                tsize = length(ttuple);
                                tupleSize = tsize;
                                if (limit == -1) {
                                    limit = (int) (2 * Runtime.getRuntime().maxMemory() / 3 / (long) (40 + tsize));
                                    if (limit < 0)
                                        limit = Integer.MAX_VALUE;
                                }
                            }

                            ++count;
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
                        //System.out.println("Wan tuo le 1, count=" + count);
                        limit = count / 2;
                        //e.printStackTrace();
                    } else if (limit == -1)
                        limit = count / 2;
                }

                if (fail)
                    break;

                //System.gc();
                try {
                    //System.out.println("Sorting the loaded " + table.size() + " elements");
                    Collections.sort(table, comparator);

                    if (!end)
                        memory = false;

                    if (memory) {
		                iter = table.iterator();
                		current = null;
                        limit = count;
                    } else {
                        //System.out.println("External sort, limit=" + limit);
                        RecordFile t = Storage.getInstance().insertTempTable(tupleSize);
                        tempTables.add(t);

                        //int per = 0;
                        for (DataEntity[] dea: table) {
                            t.insert(dea);
                            /*
                            ++per;

                            if (per % 100000 == 0)
                                System.out.println("Wrote 100 kilo elements");*/
                        }
                        table = null;
                    }
                } catch (OutOfMemoryError e) {
                    table = null;
                    //System.out.println("Wan tuo le 2, count=" + count);
                    //e.printStackTrace();
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
