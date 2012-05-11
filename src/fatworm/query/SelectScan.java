package fatworm.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import fatworm.absyn.BoolExpr;
import fatworm.dataentity.DataEntity;
import fatworm.record.RecordFile;

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
        	if (!scan.hasColumn(c) && scan.getFunctionValue(c) == null)
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
				else if (scan.getFunctionValue(column) != null)
					env.putValue(column, scan.getFunctionValue(column));
			}
			if (pred.satisfiedBy(env))
				return true;
			env.endScope();
		}
		return false;
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
	public int indexOf(String colname) {
		return scan.indexOf(colname);
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
	public RecordFile getRecordFile() {
		return scan.getRecordFile();
	}

	@Override
	public DataEntity getFunctionValue(String func) {
		return scan.getFunctionValue(func);
	}
	
	public boolean hasFunctionValue(String func) {
		return scan.hasFunctionValue(func);
	}		
}