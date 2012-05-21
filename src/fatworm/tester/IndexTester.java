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

public class IndexTester {
    public static final void main(String[] args) {
        IndexTester tester = new IndexTester();
        try {
            tester.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String dbName = "indextest";
    String tableName = "indextest";
    Random rand = new Random(0);

    public void test() throws Exception {
        Class.forName("fatworm.driver.Driver");
        String url = "jdbc:fatworm://test/";
        String username = "fatworm";
        String password = "fatworm";
        Connection con = DriverManager.getConnection(url, username, password);
        Statement sql_statement = con.createStatement();

        sql_statement.execute("drop database " + dbName);
        sql_statement.execute("create database " + dbName);
        sql_statement.execute("use " + dbName);
        sql_statement.execute("create table " + tableName + " (x int, y int)");

        int n = 100000;
        for (int i = 0; i < n; ++i)
            sql_statement.execute("insert into " + tableName + " values (" + rand.nextInt() + ", " + rand.nextInt() + ")");

        System.out.println("insert finished");

        sql_statement.execute("create index idx0 on " + tableName + "(x)");
        System.out.println("x index created");
        sql_statement.execute("create index idx1 on " + tableName + "(y)");
        System.out.println("y index created");
    }
}
