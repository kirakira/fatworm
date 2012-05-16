package fatworm.absyn;

public class ProjectionRenameValue extends ProjectionValue{
	public String alias;
	public ProjectionRenameValue(Value val, String alias){
		super(val);
		this.alias = alias.toLowerCase();
	}

	
	public String getAlias() {
		return alias;
	}
}
