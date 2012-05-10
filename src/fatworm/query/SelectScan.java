package fatworm.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import fatworm.absyn.BoolExpr;
import fatworm.dataentity.DataEntity;

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
        while (iter.hasNext()) 
        	if (!scan.hasColumn(iter.next()))
        		iter.remove();
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
				env.putValue(column, scan.getField(column));
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
}