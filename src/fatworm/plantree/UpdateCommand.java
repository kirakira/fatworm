package fatworm.plantree;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import fatworm.absyn.BoolExpr;
import fatworm.absyn.Value;
import fatworm.dataentity.DataEntity;
import fatworm.query.Env;
import fatworm.query.Scan;
import fatworm.query.SelectScan;
import fatworm.query.SimpleEnv;
import fatworm.query.TableScan;
import fatworm.record.RecordFile;
import fatworm.record.Schema;
import fatworm.util.Util;

public class UpdateCommand extends Command{
	public UpdateCommand(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public BoolExpr condition;
	public Map<String, Value> assigns = new HashMap<String, Value>();
	
	void update(RecordFile rf) {
		Map<String, DataEntity> result = new HashMap<String, DataEntity> ();
		Schema schema = rf.getSchema();
		Env env = new SimpleEnv();
		for(Entry<String, Value> assign: assigns.entrySet()) {
			result.put(assign.getKey(), assign.getValue().getValue(env).toType(schema.type(assign.getKey())));
		}
		rf.update(result);
	}
	
	public void execute() {
		TableScan scan = new TableScan(name);
		SelectScan select = new SelectScan(scan, condition, Util.getEmptyEnv());
		boolean next = select.next();
		while(next) {
			update(select.getRecordFile());
			next = select.next(); 
		}
	} 
}
