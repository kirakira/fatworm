package fatworm.plantree;

import fatworm.absyn.BoolExpr;
import fatworm.query.SelectScan;
import fatworm.query.TableScan;
import fatworm.record.RecordFile;
import fatworm.util.Util;

public class DeleteCommand extends Command{

	public DeleteCommand(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public BoolExpr condition;

	void delete(RecordFile rf) {
		rf.delete();
	}
	
	public void execute() {
		TableScan scan = new TableScan(name);
		SelectScan select = new SelectScan(scan, condition, Util.getEmptyEnv());
		select.beforeFirst();
		while(select.next()){
			delete(select.getRecordFile());
		}
	} 
}
