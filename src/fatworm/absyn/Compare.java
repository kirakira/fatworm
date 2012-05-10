package fatworm.absyn;

import fatworm.dataentity.*;
public class Compare {
    public static boolean compare(DataEntity left, DataEntity right, String cop){
    	if (left.isNull() || right.isNull()) {
    		if (left.isNull() && right.isNull() && cop.equals("="))
    			return true;
    		if (cop.equals("<>"))
    			return true;
    		return false;
    	}
        int res = left.compareTo(right);
        if (cop.equals("="))
            return res == 0;
        if (cop.equals("<"))
            return res < 0;
        if (cop.equals(">"))
            return res > 0;
        if (cop.equals("<>"))
            return res != 0;
        if (cop.equals("<="))
            return res <= 0;
        if (cop.equals(">="))
            return res >= 0;
        return false;
    }
    
}
