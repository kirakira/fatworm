package fatworm.absyn;

public class ProjectionAllColumnValue extends ProjectionValue{
	public ProjectionAllColumnValue(Value val){
		super(val);
	}
	public String toString(){
		return "ProjectionAllColumn";
	}
}
