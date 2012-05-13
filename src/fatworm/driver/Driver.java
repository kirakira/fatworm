package fatworm.driver;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.util.Properties;

import fatworm.database.Database;

public class Driver implements java.sql.Driver {

	static {
		try {
			java.sql.DriverManager.registerDriver(new Driver());
			database = Database.getInstance();
		} catch (SQLException E) {
			throw new RuntimeException("Can't register driver!");
		}
	}
	String path;
	static Database database;
	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return true;
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		path = url.substring(15);
		System.out.println(path);
		database.getStorageManager().setPath(path);
		return new FatwormConnection(database);
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		return null;
	}

	@Override
	public boolean jdbcCompliant() {
		return true;
	}

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
