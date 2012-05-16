package fatworm.tester;

import fatworm.record.Schema;
import fatworm.record.RecordFile;
import fatworm.storage.Storage;
import fatworm.dataentity.*;
import fatworm.absyn.OrderByColumn;
import fatworm.query.OrderContainer;
import fatworm.query.AdvancedOrderContainer;

import static java.sql.Types.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class SortTester {
    public static final void main(String[] args) {
        SortTester tester = new SortTester();
        try {
            tester.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String dbName = "sort";
    String tableName = "sort";
    Random rand = new Random();

    public void test() throws Exception {
        Class.forName("fatworm.driver.Driver");
        String url = "jdbc:fatworm://test/";
        String username = "fatworm";
        String password = "fatworm";
        Connection con = DriverManager.getConnection(url, username, password);
        Statement sql_statement = con.createStatement();

        int length = 10000;
        sql_statement.execute("drop database " + dbName);
        sql_statement.execute("create database " + dbName);
        sql_statement.execute("use " + dbName);
        sql_statement.execute("create table " + tableName + "(key int, value char(" + length + ")");

        int n = 100;
        for (int i = 0; i < n; ++i)
            sql_statement.execute("insert into " + tableName + " values(" + rand.nextInt(100) + ", '')");
        boolean result = sql_statement.execute("select * from " + tableName + " order by key");
        if (!result)
            System.out.println("no result");
        else {
            ResultSet resultset = sql_statement.getResultSet();
            ResultSetMetaData metadata = resultset.getMetaData();
            int width = metadata.getColumnCount();
            //resultset.beforeFirst();
            for (int i =1; i <= width;i++)
                System.out.print(metadata.getColumnType(i) + " | ");
            System.out.println();
            while(resultset.next()) {
                for (int i = 1; i <= width; i++) {
                    System.out.print(resultset.getObject(i).toString() + " | ");
                }
                System.out.println();
            }
            System.out.println();
        }

        /*
        Schema schema = new Schema();
        schema.addField("key", INTEGER, 4, true, false, false, null);
        schema.addField("value", CHAR, length, false, false, false, null);

        Storage.getInstance().createDatabase(dbName);
        Storage.getInstance().useDatabase(dbName);
        RecordFile rf = Storage.getInstance().insertTable(tableName, schema);
        
        if (rf == null)
            System.out.println("Create table failed");

        int n = 10000;
        for (int i = 0; i < n; ++i) {
            Map<String, DataEntity> map = new HashMap<String, DataEntity>();
            map.put("key", new Int(rand.nextInt(100)));
            map.put("value", new FixChar("", length));
            rf.insert(map);
        }

        TableScan scan = new TableScan(rf);
        List<OrderByColumn> orders = new ArrayList<OrderByColumn>();
        orders.add(new OrderByColumn
        OrderContainer orderContainer = new AdvancedOrderContainer(scan, orders);

        Storage.getInstance().save();*/
    }
}
