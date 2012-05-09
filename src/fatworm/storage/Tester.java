package fatworm.storage;

import fatworm.storage.bplustree.*;

public class Tester {
    public static final void main(String[] args) throws java.io.FileNotFoundException, java.io.IOException {
        Database db = new Database("test/1.db");
        db.testBucket();
        db.close();
        System.out.println("Read count: " + db.readCount + ", write count: " + db.writeCount);
    }
}
