package fatworm.storage;

import fatworm.storage.bplustree.*;
import fatworm.record.Schema;
import static java.sql.Types.*;

public class Tester {
    public static final void main(String[] args) throws java.io.FileNotFoundException, java.io.IOException {
        Schema schema = new Schema();
        schema.addField("id", INTEGER, 4);
        schema.addField("name", VARCHAR, 20);
        schema.addField("age", INTEGER, 4);

        StorageManager db = new StorageManager("test/1.db");
        db.insertTable("loli", schema);
        db.close();

        db = new StorageManager("test/1.db");
        Table t = db.getTable("loli");
        if (t == null)
            System.out.println("Table not found");
        for (String field: t.getSchema().fields())
            System.out.print(field + "\t");
        System.out.println();

        System.out.println("Read count: " + db.readCount + ", write count: " + db.writeCount);
    }
}
