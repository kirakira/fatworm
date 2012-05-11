package fatworm.absyn;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fatworm.dataentity.DataEntity;
import fatworm.query.Env;

public class ColumnValue extends Value{
	ColName colName;
	public ColumnValue(ColName colName){
		this.colName = colName;
	}

	public String toString(){
		return colName.toString();
	}
	
	@Override
	public DataEntity getValue(Env env) {
        return env.getValue(colName.toString());
        // if (colName instanceof SimpleCol)
        //     return env.getValue(((SimpleCol)colName).id);
        // else 
        //     return env.getValue(((FieldCol)colName).table, ((FieldCol)colName).col);
	}
	
    public Set<String> dumpUsefulColumns() {
    	return colName.dumpUsefulColumn();
    }

	@Override
	public int getType(Map<String, Integer> typemap) {
		return typemap.get(colName.toString());
	}

}
