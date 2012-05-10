package fatworm.absyn;

import java.util.Set;

public class ProjectionValue {
	Value val;
	ProjectionValue(Value val){
		this.val = val;
	}
	
	public Set<String> dumpUsefulColumns(){
		return val.dumpUsefulColumns();
	}
}
