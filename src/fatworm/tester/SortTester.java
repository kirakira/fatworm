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

        int length = 100000;
        sql_statement.execute("drop database " + dbName);
        sql_statement.execute("create database " + dbName);
        sql_statement.execute("use " + dbName);
        sql_statement.execute("create table " + tableName + "(k int, v char(" + length + "))");

        int n = 1000;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i)
            sb.append('a');
        String s = sb.toString();
        for (int i = 0; i < n; ++i)
            sql_statement.execute("insert into " + tableName + " values(" + rand.nextInt(100) + ", '" + s + "')");
        System.out.println("Insert finished");
        sql_statement.execute("use " + dbName);

        boolean result = sql_statement.execute("select * from " + tableName + " order by k desc");
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
        System.out.println("Total memory: " + Runtime.getRuntime().totalMemory());
    }
}
