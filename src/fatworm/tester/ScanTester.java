package fatworm.tester;

import fatworm.storage.*;
import fatworm.record.*;
import fatworm.dataentity.*;

import static java.sql.Types.*;
import java.util.Map;
import java.util.HashMap;

public class ScanTester {
    public static final void main(String[] args) {
        ScanTester tester = new ScanTester();
        tester.test();
    }

    public void test() {
        Storage storage = Storage.getInstance();
        Schema schema = new Schema();
        schema.addField("k", INTEGER, 4, false, false, false, null);
        schema.addField("value", VARCHAR, 4000, false, false, false, null);

        storage.dropDatabase("nano");
        storage.createDatabase("nano");
        storage.useDatabase("nano");
        RecordFile rf = storage.insertTable("nano", schema);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 4000; ++i)
            sb.append('a');
        String s = sb.toString();

        System.out.println("Start inserting");
        long t0 = System.nanoTime();
        for (int i = 0; i < 1000; ++i) {
            Map<String, DataEntity> map = new HashMap<String, DataEntity>();
            map.put("k", new Int(i + 1));
            map.put("value", new VarChar(s));
            rf.insert(map);
        }
        t0 = System.nanoTime() - t0;
        System.out.println("Insert finished, time = " + t0 * (1e-6) + " ms");

        System.out.println("Start enumerating");
        t0 = System.nanoTime();
        rf.beforeFirst();
        while (rf.next()) {
            DataEntity de = rf.getField("value");
        }
        t0 = System.nanoTime() - t0;
        System.out.println("Enumerating finished, time = " + t0 * (1e-6) + "ms");
    }
}
