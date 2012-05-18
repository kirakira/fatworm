package fatworm.plantree;

import java.util.LinkedList;

import fatworm.absyn.ColumnDef;
import fatworm.record.Schema;
import fatworm.util.Util;

public class CreateTable extends Command{
	Schema schema;
	
	public CreateTable(String name) {
		super(name);
	}

	public CreateTable(String tableName, Schema schema){
		super(tableName);
		this.schema = schema;
	}
	
	public void execute() {
		Util.createTable(name, schema);
		Util.createTotalIndex(name);
	}
}
