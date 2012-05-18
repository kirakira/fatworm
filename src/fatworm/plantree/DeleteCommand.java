package fatworm.plantree;

import java.io.IOException;

import fatworm.absyn.BoolExpr;
import fatworm.query.Scan;
import fatworm.query.SelectScan;
import fatworm.query.TableScan;
import fatworm.record.RecordFile;
import fatworm.record.RecordIterator;
import fatworm.util.Util;

public class DeleteCommand extends Command{

	public DeleteCommand(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public BoolExpr condition;

	void delete(RecordIterator rf)  {
		rf.remove();
	}
	
	public void execute() {
		Scan scan = new TableScan(name);
		if (condition != null)
			scan = new SelectScan(scan, condition, Util.getEmptyEnv());
		scan.beforeFirst();
		while(scan.next()){
			delete(scan.getRecordFile());
		}
	} 
}
