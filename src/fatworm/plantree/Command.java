package fatworm.plantree;

abstract public class Command {
	public String name;
	
	public Command(String name) {
		this.name = name;
	}
	
	abstract public void execute();
}
