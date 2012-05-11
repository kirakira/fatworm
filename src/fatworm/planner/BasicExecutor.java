package fatworm.planner;

import fatworm.plantree.Command;
import fatworm.record.RecordFile;
import fatworm.record.Schema;
import fatworm.util.Util;
import fatworm.plantree.*;
import fatworm.query.Scan;

public class BasicExecutor implements Executor {

	@Override
	public void execute(Command command) {
		if (command instanceof InsertCommand) {
			
			try {
				RecordFile rf = Util.getTable(command.name);
				if (command instanceof SimpleInsert || command instanceof FieldInsert) {
					rf.insert(((InsertCommand) command).getTupleMap(rf.getSchema()));
				}
				else if (command instanceof QueryInsert) {
					Scan scan = Util.getQueryPlanner().createQueryPlan(((QueryInsert) command).query).open();
					scan.beforeFirst();
					Schema  schema = rf.getSchema();
					while(scan.next())
						rf.insert(((QueryInsert) command).getTupleMap(schema, scan));
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
			command.execute();

	}

}
