package fatworm.absyn;

public class FloatValue extends Value{
	public Double fl;
	public FloatValue(Double fl){
		this.fl = fl;
	}
	public String toString(){
		return fl.toString();
	}
}
