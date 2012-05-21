package fatworm.absyn;

import fatworm.util.Util;

public class ProjectionRenameValue extends ProjectionValue{
	public String alias;
	public ProjectionRenameValue(Value val, String alias){
		super(val);
		this.alias = alias.toLowerCase();
//		Util.putRealName(alias, val.toString());
	}

	
	public String getAlias() {
		return alias;
	}
}
