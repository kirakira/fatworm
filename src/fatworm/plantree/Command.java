package fatworm.plantree;

abstract public class Command extends SqlStatement{
	public String name;
	
	public Command(String name) {
		this.name = name.toLowerCase();
	}
	
	abstract public void execute();
}
