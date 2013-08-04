package HistoricalCPUData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Maintains a connection to a HyperSQL database. Provides functionality to add CPU usage
 * data to the database.
 */
public class DBConnector {

	/**
	 * The DB connection object.
	 */
	private Connection connection;
	
	/**
	 * Creates a DB as a file located in the data/ directory.
	 */
	public DBConnector() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			connection = DriverManager.getConnection("jdbc:hsqldb:file:data/CPU_DB", "defaultuser", "");
		
			initializeDB();
		} catch (ClassNotFoundException e) {
			System.out.println("Error occurred when loading DB driver: " + e.getMessage());
		
		} catch (SQLException e) {
			System.out.println("Error occurred when connection to DB: " + e.getMessage());
		}
    }
	
	/**
	 * Creates the necessary tables if they don't already exist.
	 */
	private void initializeDB() {
		try {
			String createTblSQL = "CREATE TABLE cpu_usage ("+
				" ts BIGINT NOT NULL PRIMARY KEY," +  // Timestamp
				" usage DOUBLE NOT NULL" +           // Actual CPU usage value
				");";
			Statement st = connection.createStatement();
			st.execute(createTblSQL);
		} catch (SQLException e) {
			// If an exception is thrown, the table already exists.
		}
	}

	/**
	 * Adds the given (timestamp,usage) data to the database.
	 * @param timestamp The time when the given CPU usage data was measured.
	 * @param usage     The CPU usage value.
	 */
	public void addCpuUsage(long timestamp, double usage) {
		try {
			String insertCpuUsageSQL = "INSERT INTO cpu_usage (ts, usage) VALUES (?, ?)";
			PreparedStatement query = connection.prepareStatement(insertCpuUsageSQL);
			
			query.setLong(1, timestamp);
			query.setDouble(2, usage);
			
			int numRows = query.executeUpdate();
			
			if (numRows != 1) {
				System.out.println("Error. Value (ts=" + timestamp + ", usage=" + usage + ") could not be inserted into DB");
			}
		} catch (SQLException e) {
			System.out.println("Error occurred when attempting to insert (ts=" + timestamp + ", usage=" + usage + ") into DB: " + e.getMessage());
		}
	}
	
	/**
	 * Closes the connection to the database.
	 */
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("Error occurred when closing connection to DB: " + e.getMessage());
		}
	}
}