package fatworm.plantree;

import fatworm.util.Util;

public class CreateTable extends Command{

	public CreateTable(String name) {
		super(name);
	}

	public CreateTable()
	public void execute() {
		Util.createTable(name, schema);
	}
}
