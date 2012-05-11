package fatworm.plantree;

import fatworm.util.Util;

public class CreateDatabase extends Command {

	public CreateDatabase(String name) {
		super(name);
	}

	public void execute() {
		Util.getStorageManager().createDatabase(name);
	}

}
