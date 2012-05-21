package fatworm.tester;

import fatworm.record.*;
import fatworm.storage.Storage;
import fatworm.dataentity.*;

import static java.sql.Types.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class SpeedTester {
    public static final void main(String[] args) {
        SpeedTester tester = new SpeedTester();
        try {
            tester.testStorage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String dbName = "speed";
    String tableName = "speed";
    int n = 100000;
    int length = 100000;
    Random rand = new Random(0);

    public void testFull() throws Exception {
        Class.forName("fatworm.driver.Driver");
        String url = "jdbc:fatworm://test/";
        String username = "fatworm";
        String password = "fatworm";
        Connection con = DriverManager.getConnection(url, username, password);
        Statement sql_statement = con.createStatement();

        sql_statement.execute("drop database " + dbName);
        sql_statement.execute("create database " + dbName);
        sql_statement.execute("use " + dbName);
        sql_statement.execute("create table " + tableName + "(k int, v varchar(" + length + "), primary key(k))");

        String s = "";
        for (int i = 0; i < length; ++i)
            s += ('a');
        for (int i = 0; i < n; ++i)
            sql_statement.execute("insert into " + tableName + " values(" + rand.nextInt(100) + ", '" + "" + "')");
    }

    public void testStorage() {
        Schema schema = new Schema();
        schema.addField("k", INTEGER, 4, true, true, true, null);
        schema.addField("v", VARCHAR, length, false, false, false, null);

        Storage.getInstance().dropDatabase(dbName);
        Storage.getInstance().createDatabase(dbName);
        Storage.getInstance().useDatabase(dbName);
        RecordFile rf = Storage.getInstance().insertTable(tableName, schema);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i)
            sb.append('a');
        String s = sb.toString();
        for (int i = 0; i < n; ++i) {
            Map<String, DataEntity> map = new HashMap<String, DataEntity>();
            map.put("k", new Int(rand.nextInt(100)));
            map.put("v", new VarChar(""));
            rf.insert(map);
        }
        Storage.getInstance().save();
    }

    public void testIndex() {
        int charLength = 1024, count = 1000;

        Schema schema = new Schema();
        schema.addField("a", VARCHAR, charLength, false, false, false, new VarChar("abc"));

        Storage.getInstance().dropDatabase(dbName);
        Storage.getInstance().createDatabase(dbName);
        Storage.getInstance().useDatabase(dbName);
        RecordFile rf = Storage.getInstance().insertTable(tableName, schema);
        rf.createIndex("a");

        for (int i = 0; i < count; ++i) {
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < charLength; ++j) {
                char c = (char) ('a' + rand.nextInt(26));
                sb.append(c);
            }

            Map<String, DataEntity> map = new HashMap<String, DataEntity>();
            map.put("a", new VarChar(sb.toString()));
            rf.insert(map);
        }

        /*
        RecordIterator iter = rf.scan();
        iter.beforeFirst();
        while (iter.next())
            System.out.println(iter.getField(0).toJavaType());*/

        Storage.getInstance().save();
    }
}
