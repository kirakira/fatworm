package fatworm.plantree;

import java.util.LinkedList;

import fatworm.util.Util;

public class DropTable extends Command{

	public DropTable(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public LinkedList<String> tableList = new LinkedList<String>();
	
	@Override
	public void execute() {
		for (String table: tableList)
			Util.dropTable(table);	
	}

}
