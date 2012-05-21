package fatworm.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import fatworm.absyn.BoolExpr;
import fatworm.dataentity.DataEntity;
import fatworm.record.RecordIterator;

public class SelectScan implements Scan{
    Scan scan;
    BoolExpr pred;
    Env env;
    Set<String> usefulColumn;
    public SelectScan(Scan scan, BoolExpr pred, Env env) {
        this.scan = scan;
        this.pred = pred;
        this.env = env;
        usefulColumn = pred.dumpUsefulColumns();
        Iterator<String> iter = usefulColumn.iterator();
        while (iter.hasNext()) {
        	String c = iter.next();
        	if (!scan.hasColumn(c) && !scan.hasFunctionValue(c))
        		iter.remove();
        }
    }

	@Override
	public void beforeFirst() {
        scan.beforeFirst();
	}

	@Override
	public boolean next() {
		while(scan.next()) {
			env.beginScope();
			for(String column: usefulColumn) {
				if (scan.hasColumn(column))
					env.putValue(column, scan.getColumn(column));
				else if (scan.hasFunctionValue(column))
					env.putValue(column, scan.getFunctionValue(column));
			}
			if (pred.satisfiedBy(env))
				return true;
			env.endScope();
		}
		return false;
//		long timeconsume= 0;
//		while(true) {
//		    long start = System.nanoTime();
//		    if (!scan.next())
//		        break;
//		    timeconsume += System.nanoTime() - start;
//			env.beginScope();
//			for(String column: usefulColumn) {
//				if (scan.hasColumn(column))
//					env.putValue(column, scan.getColumn(column));
//				else if (scan.hasFunctionValue(column))
//					env.putValue(column, scan.getFunctionValue(column));
//			}
//			if (pred.satisfiedBy(env))
//				return true;
//			env.endScope();
//		}
//		System.out.println(timeconsume);
//		return false;
	}

	@Override
	public DataEntity getField(String fldname) {
		return scan.getField(fldname);
	}

	@Override
	public boolean hasField(String fldname) {
		return scan.hasField(fldname);
	}

	@Override
	public boolean hasColumn(String colname) {
		return scan.hasColumn(colname);
	}

	@Override
	public Collection<String> fields() {
		return scan.fields();
	}

	@Override
	public Collection<String> columns() {
		return scan.columns();
	}

	@Override
	public DataEntity getColumnByIndex(int index) {
		return scan.getColumnByIndex(index);
	}

	@Override
	public int getNumberOfColumns() {
		return scan.getNumberOfColumns();
	}

	@Override
	public DataEntity getColumn(String colname) {
		return scan.getColumn(colname);
	}

	@Override
	public int indexOfField(String field) {
		return scan.indexOfField(field);
	}

	@Override
	public int indexOfColumn(String colname) {
		return scan.indexOfColumn(colname);
	}

	@Override
	public int type(String colname) {
		return scan.type(colname);
	}

	@Override
	public int type(int index) {
		return scan.type(index);
	}

	@Override
	public String fieldName(int index) {
		return fieldName(index);
	}

	@Override
	public String columnName(int index) {
		return columnName(index);
	}

	@Override
	public RecordIterator getRecordFile() {
		return scan.getRecordFile();
	}

	@Override
	public DataEntity getFunctionValue(String func) {
		return scan.getFunctionValue(func);
	}
	
	public boolean hasFunctionValue(String func) {
		return scan.hasFunctionValue(func);
	}
	
	@Override
	public DataEntity getOrderKey(String key) {
		return getColumn(key);
	}

    @Override
    public boolean hasIndex(String colname) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public RecordIterator getIndex(String colname, DataEntity right,
            String cop) {
        // TODO Auto-generated method stub
        return null;
    }			
}
