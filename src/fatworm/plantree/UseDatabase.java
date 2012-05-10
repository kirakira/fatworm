package fatworm.plantree;

import fatworm.util.Util;

public class UseDatabase extends Command {

	public UseDatabase(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		Util.getStorageManager().useDatabase(name);
	}

}
