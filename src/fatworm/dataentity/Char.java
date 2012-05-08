package fatworm.dataentity;

public class Char {
    String value;
    int length;
    public Char(String s, int l){
        value = s;
        length = l;
    }
    public int compareTo(DataEntity t){
        return 0;
    }

}