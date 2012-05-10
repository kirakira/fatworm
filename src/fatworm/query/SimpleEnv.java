package fatworm.query;

import java.util.HashMap;

import fatworm.dataentity.DataEntity;

public class SimpleEnv implements Env {

	HashMap<String,DataEntity> map;
	
	public SimpleEnv() {
		map = new HashMap<String, DataEntity>();
	}
	@Override
	public DataEntity getValue(String colname) {
		return map.get(colname);
	}

	@Override
	public void  putValue(String colname, DataEntity data) {
		map.put(colname, data);
	}
	@Override
	public void beginScope() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void endScope() {
		// TODO Auto-generated method stub
		
	}
	
	

}
