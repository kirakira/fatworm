package fatworm.storage;

import fatworm.storagemanager.*;
import fatworm.record.RecordFile;
import fatworm.record.Schema;

import java.io.File;

public class Storage implements StorageManagerInterface {
    private Database current = null, tempDB = null;
    private static String tempDBName = "TEMP";
    private String currentName = "";
    private String path = "test" + java.io.File.separator;

    private int tempCount = 0;

    private static Storage instance = null;

    private Storage() {
    }

    private Database tempDB() {
        if (tempDB != null)
            return tempDB;
        else {
            File f = new File(fileName(tempDBName, false));

            try {
                if (f.exists())
                    f.delete();
                tempDB = Database.create(fileName(tempDBName, false));
                tempDB.save();
                return tempDB;
            } catch (java.io.IOException e) {
                return null;
            }
        }
    }

    public static Storage getInstance() {
        if (instance == null)
            instance = new Storage();
        return instance;
    }

    private String fileName(String dbName) {
        return fileName(dbName, true);
    }

    private String fileName(String dbName, boolean suffix) {
        if (suffix)
            return path + dbName + ".db";
        else
            return path + dbName;
    }

    public boolean createDatabase(String name) {
        File f = new File(fileName(name));
        if (f.exists())
            return false;
        else {
            try {
                Database db = Database.create(fileName(name));
                db.close();
                return true;
            } catch (java.io.FileNotFoundException e) {
                return false;
            } catch (java.io.IOException e) {
                return false;
            }
        }
    }

	public boolean useDatabase(String name) {
        if (current != null && currentName.equals(name))
            return true;

        File f = new File(fileName(name));
        if (!f.exists())
            return false;

        Database db = null;
        try {
            db = Database.load(fileName(name));
        } catch (java.io.IOException e) {
            return false;
        }

        if (current != null)
            current.close();

        current = db;
        currentName = name;

        return true;
    }

    public boolean dropDatabase(String name) {
        if (current != null && currentName.equals(name)) {
            current.close();
            current = null;
        }

        File file = new File(fileName(name));
        if (!file.exists())
            return false;

        if (!file.delete())
            return false;

        return true;
    }

    public RecordFile getTable(String tablename) {
        if (current == null)
            return null;

        try {
            return current.getTable(tablename);
        } catch (java.io.IOException e) {
            return null;
        }
    }

	public RecordFile insertTable(String tablename, Schema schema) {
        if (current == null)
            return null;

        try {
            return current.insertTable(tablename, schema);
        } catch (java.io.IOException e) {
            return null;
        }
    }

    public RecordFile insertTempTable(int tupleSize) {
        Table table;
        try {
            table = tempDB().insertTable("t" + tempCount, tupleSize);
        } catch (java.io.IOException e) {
            return null;
        }
        ++tempCount;
        return table;
    }

	public void dropTable(String name) {
        if (current == null)
            return;

        try {
            current.dropTable(name);
        } catch (java.io.IOException e) {
        }
    }

    public void save() {
        if (current == null)
            return;

        try {
            current.save();
        } catch (java.io.IOException e) {
        }
    }

    public void setPath(String path) {
        if (!path.endsWith(java.io.File.separator))
            path += java.io.File.separator;
        this.path = path;
    }

    public IOHelper getCurrentIOHelper() {
        return current;
    }
}
