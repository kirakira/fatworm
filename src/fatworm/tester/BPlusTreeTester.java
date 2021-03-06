package fatworm.tester;

import fatworm.dataentity.*;
import fatworm.storage.Storage;
import fatworm.storage.IOHelper;
import fatworm.storage.DataAdapter;
import fatworm.storage.bplustree.*;

import java.util.Random;
import java.util.List;
import java.util.LinkedList;
import static java.sql.Types.*;

public class BPlusTreeTester {
    public static final void main(String[] args) throws java.io.IOException {
        BPlusTreeTester test = new BPlusTreeTester();
        test.test();
    }

    Random rand = new Random(0);
    private String dbName = "bptree";

    public void test() throws java.io.IOException {
        Storage storage = Storage.getInstance();
        storage.dropDatabase(dbName);
        storage.createDatabase(dbName);
        storage.useDatabase(dbName);
        IOHelper io = storage.getCurrentIOHelper();

        DataAdapter da = new DataAdapter(INTEGER, 4);

        long t0 = System.nanoTime();
        BPlusTree bptree = BPlusTree.create(io, da.comparator(), da.averageKeySize(), da.isVariant());
        int block = bptree.save();
        //int block = BPlusTree.create(io, new StringComparator(), BPlusTree.KeySize.VARIANT).getBlock();
        bptree = BPlusTree.load(io, da.comparator(), block);
        //BPlusTree bptree = BPlusTree.load(io, new StringComparator(), block);
        if (bptree == null)
            System.err.println("bptree == null");
        else {
            int range = 100000;
            List<Integer>[] data = new List[range];
            for (int i = 0; i < range; ++i)
                data[i] = new LinkedList<Integer>();
            for (int i = 0; i < 100000; ++i) {
                int t = rand.nextInt(range);
                data[t].add(new Integer(i));
                bptree.insert(da.putData(new Int(t)), i);
            }
            t0 = System.nanoTime() - t0;
            if (bptree.check() == false)
                System.out.println("check failed");
            else
                System.out.println("check ok");

            for (int i = 0; i < range; ++i) {
                byte[] tmp = da.putData(new Int(i));
                List<Integer> result = bptree.find(tmp);
                if (!result.containsAll(data[i]) || !data[i].containsAll(result)) {
                    System.out.println("Data error");
                    System.out.println("Return value:");
                    for (Integer ii: result)
                        System.out.print(ii + " ");
                    System.out.println();
                    System.out.println("Raw data:");
                    for (Integer ii: data[i])
                        System.out.print(ii + " ");
                    System.out.println();
                } else {
                    System.out.println("check ok, contains " + result.size() + " values");
                }
                if (result.size() > 0) {
                    System.out.println("Removing " + i);
                    for (Integer value: result) {
                        bptree.remove(tmp, value);
                    }
                    /*
                    if (bptree.check() == false)
                        System.out.println("check failed");
                    else
                        System.out.println("check after remove ok");*/
                }
            }
            if (bptree.check() == false)
                System.out.println("check failed");
            /*
            Set<String> data = new HashSet<String>();
            for (int i = 0; i < 10000; ++i) {
                int l = rand.nextInt(100);
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < l; ++j)
                    sb.append((char) (48 + rand.nextInt(26)));

                data.add(sb.toString());
                bptree.insert(ByteLib.stringToBytes(sb.toString()), i);
            }
            t0 = System.nanoTime() - t0;
            if (bptree.check() == false)
                System.out.println("check failed");
            else
                System.out.println("check ok, data count: " + data.size());

            System.out.println("Total time: " + t0);
            System.out.println("Disk IO time: " + File.totalTime + " (" + (double) File.totalTime / (double) t0 * 100 + "%)");*/

            bptree.save();
        }
    }
}
