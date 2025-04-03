package com.integra.DB;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import com.integra.util.Util;

public class JDBC_Connection {

	public static final String JDBC_URL = "JDBC_URL";
	public static final String JDBC_USERNAME = "JDBC_USERNAME";
	public static final String JDBC_PASSWORD = "JDBC_PASSWORD";

	private Connection connection;

	// Getter method for the connection
	public Connection getConnection() {
		return this.connection;
	}

	// Constructor to establish the JDBC connection using properties from the file
	public JDBC_Connection(File propertiesFile) throws SQLException {
		try {
			// Get the properties from the db.properties file
			System.out.println("Loading properties from db.properties file...");
			Map<String, String> dbProperties = Util.getDatabaseProperties(propertiesFile);

			System.out.println("Properties loaded from db.properties file:");
			for (Map.Entry<String, String> entry : dbProperties.entrySet()) {
				System.out.println(entry.getKey() + " = " + entry.getValue());
			}

			// Get the required properties from the map
			String dbUrl = dbProperties.get(JDBC_URL);
			String dbUsername = dbProperties.get(JDBC_USERNAME);
			String dbPassword = dbProperties.get(JDBC_PASSWORD);

			// Validate if the necessary properties are present
			if (dbUrl == null || dbUrl.trim().isEmpty()) {
				throw new SQLException("Error: Database URL is missing or empty.");
			}
			if (dbUsername == null || dbUsername.trim().isEmpty()) {
				throw new SQLException("Error: Database username is missing or empty.");
			}
			if (dbPassword == null || dbPassword.trim().isEmpty()) {
				throw new SQLException("Error: Database password is missing or empty.");
			}

			// Establish the JDBC connection
			this.connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);


		} catch (Exception e) {
			// Handle exceptions
			System.out.println("Error loading properties or establishing connection: " + e.getMessage());
			e.printStackTrace();
			throw new SQLException("Unable to establish database connection", e);
		}
	}

	public JDBC_Connection() {
		// TODO Auto-generated constructor stub
	}

	// Close the connection
	public void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println("Error closing connection: " + e.getMessage());
		}
	}

	// Main method to test the connection
	public static void main(String[] args) {
		// Example usage for establishing JDBC connection using properties from the file
		File propertiesFile = new File("db.properties");

		try {
			JDBC_Connection jdbcConnection = new JDBC_Connection(propertiesFile); // Create instance with properties
																					// file
			System.out.println("Connection established successfully!");

			// Access the connection
			Connection connection = jdbcConnection.getConnection();

			// Check if the connection is valid
			if (connection != null) {
				System.out.println("Successfully connected to the database.");
				jdbcConnection.closeConnection(); // Close connection after use
				System.out.println("Connection closed successfully.");
			}
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
