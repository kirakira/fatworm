package fatworm.query;

public class DependentCondition {
    String myleft;
    String yourright;
    String cop;

    public DependentCondition(String my, String your, String cop) {
        this.myleft = my;
        this.yourright = your;
        this.cop = cop;
    }
    
}