package fatworm.absyn;

public class ProjectionSimpleValue extends ProjectionValue{
	public ProjectionSimpleValue(Value val){
		super(val);
	}
	public String toString(){
		return "ProjectionSimple "+val.toString();
	}
}
