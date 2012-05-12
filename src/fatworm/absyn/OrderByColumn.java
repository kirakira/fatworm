package fatworm.absyn;

public class OrderByColumn {
	public ColName col;
	boolean desc;
	public OrderByColumn (ColName col, boolean desc) {
		this.col = col;
		this.desc = desc;
	}
}
