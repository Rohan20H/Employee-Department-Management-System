package com.integra.DB;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.Statement;
import com.integra.Exception.CustomeException;
import com.integra.dto.DepartmentDTO;

public class BaseDAO {

	protected static JDBC_Connection jdbcConnection;

	// Constructor to initialize the JDBC_Connection
	public BaseDAO(JDBC_Connection jdbcConnection) {
		this.jdbcConnection = jdbcConnection;
	}

	// Method to close the Statement resource
	protected void closeResource(Statement statement) throws CustomeException {
		if (statement != null) {
			try {
				statement.close();
			} catch (Exception e) {
				throw new CustomeException(CustomeException.ERROR_INVALID_DB_CONFIG, "Unable to close statement", e);
			}
		}
	}

	// Method to close the ResultSet resource
	protected void closeResource(ResultSet res) {
		if (res != null) {
			try {
				res.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Method to close both Statement and ResultSet resources
	protected void closeResource(Statement statement, ResultSet res) throws CustomeException {
		this.closeResource(statement);
		this.closeResource(res);
	}

	// Getter for the JDBC_Connection
	public static JDBC_Connection getJDBC_Connection() {
		return jdbcConnection;
	}

	public boolean updateDepartment(DepartmentDTO department) throws CustomeException {

		return false;
	}
}
