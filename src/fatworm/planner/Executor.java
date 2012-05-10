package fatworm.planner;

import fatworm.plantree.Command;
public interface Executor {

	public void execute(Command command);
}
