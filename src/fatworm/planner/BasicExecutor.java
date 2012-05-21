package fatworm.planner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fatworm.dataentity.DataEntity;
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
					List<Map<String, DataEntity>> result = new LinkedList<Map<String, DataEntity>>();
					while(scan.next())
						result.add(((QueryInsert) command).getTupleMap(schema, scan));
					for(Map<String, DataEntity> tuple: result) {
					    rf.insert(tuple);
					    for (Entry<String, DataEntity> column: tuple.entrySet())
					        System.out.println(column.getKey() +" " + column.getValue());
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
			command.execute();
		
		//Util.getStorageManager().save();
	}

}
