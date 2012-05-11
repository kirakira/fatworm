package fatworm.plantree;

import fatworm.util.Util;

public class DropTable extends Command{

	public DropTable(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		Util.dropTable(name);
	}

}
