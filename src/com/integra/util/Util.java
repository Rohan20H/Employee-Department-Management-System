package com.integra.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.integra.Exception.CustomeException;

public class Util {

	public Util() {
	}

	// Method to load database properties from a file with additional validation
	public static Map<String, String> getDatabaseProperties(File filename) throws CustomeException {
		Map<String, String> propertiesMap = null;
		FileInputStream fileInputStream = null;

		// Validate the file
		validateFile(filename);

		try {
			// Create an input stream for the given file
			fileInputStream = new FileInputStream(filename);

			// Load properties from the input stream
			Properties properties = new Properties();
			properties.load(fileInputStream);
//string constant, error message, print value
			// Validate if properties are loaded
			if (properties.isEmpty()) {
				throw new CustomeException(CustomeException.DATABASE_FILE_NOT_FOUND, "Error Database File not found");
			}

			// Initialize propertiesMap if properties are valid
			propertiesMap = new HashMap<>();

			// Convert the Properties to a Map<String, String>
			for (String key : properties.stringPropertyNames()) {
				String value = properties.getProperty(key);

				// Validate null or empty values
				if (value == null || value.trim().isEmpty()) {
					System.out.println("Warning: The property " + key + " has an empty or null value.");
				} else {
					propertiesMap.put(key, value);
				}
			}

		} catch (IOException e) {
			throw new CustomeException(CustomeException.IO_EXCEPTION_OCCURRED,
					"Error: An IOException occurred while reading the properties file.");
		} catch (CustomeException e) {
			throw e; // Re-throw the custom exception
		} catch (Exception e) {
			throw new CustomeException(CustomeException.UNEXPECTED_ERROR_OCCURRED, "Error: An unexpected error occurred.");
		} finally {
			// Ensure the file input stream is closed
			closeStream(fileInputStream);
		}

		// Validate propertiesMap after loading
		if (propertiesMap == null || propertiesMap.isEmpty()) {
			throw new CustomeException(CustomeException.PROPERTIES_NOT_LOADED,
					"Error: No valid properties were loaded from the file.");
		}

		return propertiesMap;
	}

	// Method to validate the file before loading
	private static void validateFile(File filename) throws CustomeException {

		if (!filename.exists()) {
			throw new CustomeException(CustomeException.DATABASE_FILE_NOT_FOUND, "Error: The file does not exist.");
		}

		if (!filename.canRead()) {
			throw new CustomeException(CustomeException.NOT_READABLE, "Error: The file is not readable.");
		}
	}

	// Helper method to close the file input stream
	private static void closeStream(FileInputStream fileInputStream) {
		if (fileInputStream != null) {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				System.out.println("Error: Failed to close the file input stream.");
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// Example usage with exception handling
		File propertiesFile = new File("db.properties");

		try {
			// Load properties
			Map<String, String> dbProperties = getDatabaseProperties(propertiesFile);

			// Print properties if available
			for (Map.Entry<String, String> entry : dbProperties.entrySet()) {
				System.out.println(entry.getKey() + " = " + entry.getValue());
			}
		} catch (CustomeException e) {
			// Handle custom exception
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public static Connection getConnection(Map<String, String> databaseProperties) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Map<String, String> getDbMap(String dbProperties) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void validateDbProperties(Map<String, String> dbProperties) throws IllegalArgumentException {
		// Validate that all required keys exist and are not empty
		if (dbProperties.get("JDBC_URL") == null || dbProperties.get("JDBC_URL").isEmpty()) {
			throw new IllegalArgumentException("Database URL is missing or empty.");
		}
		if (dbProperties.get("JDBC_USERNAME") == null || dbProperties.get("JDBC_USERNAME").isEmpty()) {
			throw new IllegalArgumentException("Database username is missing or empty.");
		}
		if (dbProperties.get("JDBC_PASSWORD") == null || dbProperties.get("JDBC_PASSWORD").isEmpty()) {
			throw new IllegalArgumentException("Database password is missing or empty.");
		}
		if (dbProperties.get("JDBC_DRIVER") == null || dbProperties.get("JDBC_DRIVER").isEmpty()) {
			throw new IllegalArgumentException("JDBC driver class is missing or empty.");
		}

	}
}
