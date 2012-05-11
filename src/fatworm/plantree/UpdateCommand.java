package fatworm.plantree;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	public Set<String> usefulColumns = new HashSet<String>();
	void update(RecordFile rf, Scan scan) {
		Map<String, DataEntity> result = new HashMap<String, DataEntity> ();
		Schema schema = rf.getSchema();
		Env env = new SimpleEnv();
		env.beginScope();
		for (String column: usefulColumns) {
			env.putValue(column, scan.getColumn(column));
		}
		for(Entry<String, Value> assign: assigns.entrySet()) {
			result.put(assign.getKey(), assign.getValue().getValue(env).toType(schema.type(assign.getKey())));
		}
		env.endScope();
		try {
			rf.update(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void execute() {
		for(Value value: assigns.values()) 
			usefulColumns.addAll(value.dumpUsefulColumns());
		TableScan scan = new TableScan(name);
		scan.beforeFirst();
		Scan select;
		if (condition == null) {
			select = scan;
		}
		else 
			select = new SelectScan(scan, condition, Util.getEmptyEnv());
		boolean next = select.next();
		while(next) {
			update(select.getRecordFile(), select);
			next = select.next(); 
		}
	} 
}
