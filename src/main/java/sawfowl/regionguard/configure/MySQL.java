package sawfowl.regionguard.configure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import sawfowl.regionguard.RegionGuard;

public class MySQL {

	private final RegionGuard plugin;
	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;
	private final String ssl;
	private Connection connection;

	public MySQL(RegionGuard instance, String hostname, String port, String database, String username, String password, String ssl) {
		this.plugin = instance;
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
		this.ssl = ssl;
		this.connection = null;
	}

	public Connection openConnection() {
		String url = "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database;
        Properties properties = new Properties();
        properties.setProperty("user", this.user);
        properties.setProperty("password", this.password);
        properties.setProperty("useSSL", this.ssl);
		try {
			this.connection = DriverManager.getConnection(url, properties);
		} catch (SQLException e) {
			plugin.getLogger().error("JDBC Driver not found!");
			plugin.getLogger().error(e.getMessage());
		}
		return this.connection;
	}
	
	public boolean checkConnection() {
		return this.connection != null;
	}

	public Connection getConnection() {
		return this.connection;
	}

	public Connection getOrOpenConnection() {
		if(!checkConnection()) return openConnection();
		try {
			if(connection.isClosed()) return openConnection();
		} catch (SQLException e) {
			plugin.getLogger().error(e.getMessage());
		}
		return null;
	}

	public void closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				plugin.getLogger().error("Error closing the MySQL Connection!");
				plugin.getLogger().error(e.getMessage());
			}
		}
	}

}
