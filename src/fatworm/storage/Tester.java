package fatworm.storage;

public class Tester {
    public static final void main(String[] args) throws java.io.FileNotFoundException, java.io.IOException {
        Database db = new Database("test/1.db");

        db.close();
    }
}
