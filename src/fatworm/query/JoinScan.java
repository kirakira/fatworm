package fatworm.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fatworm.dataentity.DataEntity;

public class JoinScan implements Scan{

    List<Scan> scanList;
    int width;
    public JoinScan(List<Scan> scanList) {
        this.scanList = scanList;
        width = 0;
        for (Scan s: scanList) {
            width += s.getNumberOfColumns();
        }
    }
	@Override
	public void beforeFirst() {
		for (Scan s: scanList)
			s.beforeFirst();
	}
	@Override
	public boolean next() {
        for (int i = scanList.size() - 1; i >0; i--) {
            if(scanList.get(i).next())
                return true;
            else 
                scanList.get(i).beforeFirst();
        }
        return scanList.get(0).next();
	}
	@Override
	public DataEntity getField(String fldname) {
        for (Scan s: scanList) {
            if (s.hasField(fldname))
                return s.getField(fldname);
        }
        return null;
	}
	@Override
	public boolean hasField(String fldname) {
        for (Scan s: scanList) {
            if (s.hasField(fldname))
                return true;
        }
		return false;
	}
	@Override
	public boolean hasColumn(String colname) {
        for (Scan s: scanList) {
            if (s.hasColumn(colname))
                return true;
        }
		return false;
	}
	@Override
	public Collection<String> fields() {
        Set<String> result = new HashSet<String>();
        for (Scan s: scanList)
            result.addAll(s.fields());
        return result;
	}
	@Override
	public Collection<String> columns() {
        Set<String> result = new HashSet<String>();
        for (Scan s: scanList)
            result.addAll(s.columns());
        return result;
	}
	@Override
	public DataEntity getColumnByIndex(int index) {
        for (Scan s: scanList) {
            int number = s.getNumberOfColumns();
            if (number <= index)
                index -= number;
            else 
                return s.getColumnByIndex(index);
        }
        return null;
	}
	@Override
	public int getNumberOfColumns() {
 		return width;
	}
	@Override
	public DataEntity getColumn(String colname) {
        for (Scan s: scanList) {
            if (s.hasColumn(colname))
                return s.getColumn(colname);
        }
		return null;
	}
}