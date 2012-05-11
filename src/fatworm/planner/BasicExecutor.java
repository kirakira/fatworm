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
			RecordFile rf = Util.getTable(command.name);
			if (command instanceof SimpleInsert || command instanceof FieldInsert) {
				rf.insert(((SimpleInsert) command).getTupleMap(rf.getSchema()));
			}
			else if (command instanceof QueryInsert) {
				Scan scan = Util.getQueryPlanner().createQueryPlan(((QueryInsert) command).query).open();
				scan.beforeFirst();
				Schema  schema = rf.getSchema();
				while(scan.next())
					rf.insert(((QueryInsert) command).getTupleMap(schema, scan));
			}
		}
		else if (command instanceof UpdateCommand || command instanceof DeleteCommand) {
			command.execute();
		}
	}

}
