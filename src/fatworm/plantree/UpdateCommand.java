package fatworm.plantree;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import fatworm.absyn.BoolExpr;
import fatworm.absyn.ConstDefault;
import fatworm.absyn.Value;
import fatworm.dataentity.DataEntity;
import fatworm.query.Env;
import fatworm.query.Scan;
import fatworm.query.SelectScan;
import fatworm.query.SimpleEnv;
import fatworm.query.TableScan;
import fatworm.record.RecordFile;
import fatworm.record.RecordIterator;
import fatworm.record.Schema;
import fatworm.util.Pair;
import fatworm.util.Util;

public class UpdateCommand extends Command{
	public UpdateCommand(String name) {
		super(name);
	}

	public BoolExpr condition;
	private List<Pair<String, Value>> assigns = new LinkedList<Pair<String, Value>>();
	//public Set<String> usefulColumns = new HashSet<String>();
	static long timeconsume = 0;
	void update(Schema schema, RecordIterator iter, Scan scan) {
		Env env = new SimpleEnv();

		for(Pair<String, Value> assign: assigns) {
		    
		    Map<String, DataEntity> result = new HashMap<String, DataEntity> ();
			if (assign.getSecond() instanceof ConstDefault)
				result.put(assign.getFirst(), schema.defaultValue(assign.getFirst()));
			else {
			    env.beginScope();			    
			    for (String column: assign.getSecond().dumpUsefulColumns()) {
			        env.putValue(column, scan.getColumn(column));
			    }
			    result.put(assign.getFirst(), assign.getSecond().getValue(env).toType(schema.type(assign.getFirst())));
			    env.endScope();
			}
			
			iter.update(result);


		}
	}
	
	public void addAssign(String key, Value value) {
	    
		assigns.add(new Pair<String, Value>(key.toLowerCase(), value));
	}
	
	public void execute() {
	    
		Scan scan = new TableScan(name);
		Schema schema = ((TableScan)scan).getSchema();
		if (condition != null) 
			scan = new SelectScan(scan, condition, Util.getEmptyEnv());
		scan.beforeFirst();
		boolean next = scan.next();
		while(next) {
			update(schema, scan.getRecordFile(), scan);
			next = scan.next();
		}
	} 
}
