package fatworm.absyn;

public class OrderByColumn {
	ColName col;
	boolean desc;
	public OrderByColumn (ColName col, boolean desc) {
		this.col = col;
		this.desc = desc;
	}
	public String toString(){
		String ans = col.toString()+" ";
		if (desc) 
			ans += "DESC";
		else
			ans += "ASC";
		return ans;
	}
}
