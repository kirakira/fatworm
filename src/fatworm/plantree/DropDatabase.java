package fatworm.plantree;

import fatworm.util.Util;

public class DropDatabase extends Command{

	public DropDatabase(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		Util.getStorageManager().dropDatabase(name);
	}

}
