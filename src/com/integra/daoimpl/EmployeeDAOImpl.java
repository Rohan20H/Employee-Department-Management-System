package com.integra.daoimpl;

import com.integra.dao.IEmployee;
import com.integra.dto.EmployeeDTO;
import com.integra.DB.BaseDAO;
import com.integra.DB.JDBC_Connection;
import com.integra.Exception.CustomeException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class EmployeeDAOImpl extends BaseDAO implements IEmployee {

	public EmployeeDAOImpl(JDBC_Connection jdbcConnection) {
		super(jdbcConnection);
	}

	
	public boolean isAdminUser(String username, String password) throws SQLException {
	    String query = "SELECT * FROM employee WHERE employee_id = ? AND password = ? AND is_admin = 1";
	    PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;

	    try {
	        preparedStatement = jdbcConnection.getConnection().prepareStatement(query);
	        preparedStatement.setString(1, username);
	        preparedStatement.setString(2, password);
	        resultSet = preparedStatement.executeQuery();
	        return resultSet.next(); // Return true if the admin user exists
	    } finally {
	        // Close resources manually
	        if (resultSet != null) {
	            try {
	                resultSet.close();
	            } catch (SQLException e) {
	                System.err.println("Failed to close ResultSet: " + e.getMessage());
	            }
	        }
	        if (preparedStatement != null) {
	            try {
	                preparedStatement.close();
	            } catch (SQLException e) {
	                System.err.println("Failed to close PreparedStatement: " + e.getMessage());
	            }
	        }
	    }
	}

	    // Method to check if the user is valid (non-admin)
	public boolean isValidUser(String username, String password) throws SQLException {
	    String query = "SELECT * FROM employee WHERE employee_id = ?";
	    PreparedStatement stmt = null;
	    ResultSet rs = null;

	    try {
	        stmt = jdbcConnection.getConnection().prepareStatement(query);
	        stmt.setString(1, username);

	        // Execute the query to fetch user data
	        rs = stmt.executeQuery();
	        if (rs.next()) {
	            String storedPassword = rs.getString("password");
	            boolean isAdmin = rs.getBoolean("is_admin");

	            // If it's an admin user, compare the password directly (no Base64)
	            if (isAdmin) {
	                return storedPassword.equals(password); // Admin password comparison
	            } else {
	                // For regular users, encode the input password to Base64 and compare with the stored Base64 password
	                String encodedInputPassword = Base64.getEncoder().encodeToString(password.getBytes());
	                return storedPassword.equals(encodedInputPassword);
	            }
	        }
	    } finally {
	        // Close resources manually
	        if (rs != null) {
	            try {
	                rs.close();
	            } catch (SQLException e) {
	                System.err.println("Failed to close ResultSet: " + e.getMessage());
	            }
	        }
	        if (stmt != null) {
	            try {
	                stmt.close();
	            } catch (SQLException e) {
	                System.err.println("Failed to close PreparedStatement: " + e.getMessage());
	            }
	        }
	    }
	    return false; // Return false if no user is found or error occurs
	}

	    
	    
	@Override
	public boolean addEmployee(EmployeeDTO employeeDTO) throws CustomeException {
		String checkQuery = "SELECT COUNT(*) FROM employee WHERE employee_id = ?";
		PreparedStatement stmt = null; // Declare PreparedStatement outside try block

		try {
			// Check if employee_id already exists in the database
			stmt = jdbcConnection.getConnection().prepareStatement(checkQuery);
			stmt.setString(1, employeeDTO.getEmployeeId());
			ResultSet rs = stmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				throw new CustomeException("Employee ID already exists. Please use a unique Employee ID.");
			}
			 // Step 2: Determine password (bcrypt hashing instead of Base64)
            String password;
            if ("Rohan".equalsIgnoreCase(employeeDTO.getEmployeeName())) {
                password = "Rohan@20";  // Fixed password for admin
            } else {
                password = employeeDTO.getEmployeeName().toUpperCase();  // Uppercase username for others
            }

            
         // Step 3: Encode the password using Base64
            String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());

			// Insert the new employee into the database if employee_id is unique
			String insertQuery = "INSERT INTO employee (employee_id, employee_name, department_id, employee_qualification, phone_number, marital_status, gender, email_id, is_admin, password) VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?)";
			stmt = jdbcConnection.getConnection().prepareStatement(insertQuery);
			stmt.setString(1, employeeDTO.getEmployeeId());
			stmt.setString(2, employeeDTO.getEmployeeName());
			stmt.setInt(3, employeeDTO.getDepartmentId());
			stmt.setString(4, employeeDTO.getEmployeeQualification());
			stmt.setString(5, employeeDTO.getPhoneNumber());
			stmt.setString(6, employeeDTO.getMaritalStatus());
			stmt.setString(7, employeeDTO.getGender());
			stmt.setString(8, employeeDTO.getEmailId());
			stmt.setBoolean(9, employeeDTO.isAdmin());
            stmt.setString(10, encodedPassword);

			// Execute the insert statement
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0; // Return true if insert is successful

		} catch (SQLException e) {
			e.printStackTrace();
			throw new CustomeException("Error adding Employee: " + e.getMessage(), e);
		} finally {
			// Ensure that the PreparedStatement is closed
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					throw new CustomeException("Failed to close PreparedStatement", e);
				}
			}
		}

	}

	@Override
	public EmployeeDTO getEmployeeById(String employeeId) throws CustomeException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = jdbcConnection.getConnection();
			String sql = "SELECT * FROM employee WHERE employee_id = ? AND is_delete = 'NO'"; // Added soft-delete
																								// condition
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, employeeId);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return new EmployeeDTO(resultSet.getString("employee_id"), resultSet.getString("employee_name"),
						resultSet.getInt("department_id"), resultSet.getString("employee_qualification"),
						resultSet.getString("phone_number"), resultSet.getString("marital_status"),
						resultSet.getString("gender"), resultSet.getString("email_id"));
			} else {
				throw new CustomeException("Employee with ID " + employeeId + " not found or is marked as deleted.");
			}
		} catch (SQLException e) {
			// Log or handle more specific SQL exceptions if needed
			throw new CustomeException("Error retrieving employee by ID: " + e.getMessage(), e); // Passing the
																								// SQLException
		} catch (Exception e) {
			// Catch any other unexpected exceptions
			throw new CustomeException("Unexpected error occurred while retrieving employee by ID: " + e.getMessage(), e);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					throw new CustomeException("Failed to close ResultSet", e);
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					throw new CustomeException("Failed to close PreparedStatement", e);
				}
			}
		}
	}

	@Override
	public List<EmployeeDTO> getAllEmployees() throws CustomeException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = jdbcConnection.getConnection();
			String sql = "SELECT * FROM employee WHERE is_delete = 'NO'"; // Fetch only non-deleted employees
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();

			List<EmployeeDTO> employees = new ArrayList<>();
			while (resultSet.next()) {
				// Mapping the result set to EmployeeDTO
				EmployeeDTO employee = new EmployeeDTO(resultSet.getString("employee_id"),
						resultSet.getString("employee_name"), resultSet.getInt("department_id"), // department_id is
						resultSet.getString("employee_qualification"), resultSet.getString("phone_number"),
						resultSet.getString("marital_status"), resultSet.getString("gender"),
						resultSet.getString("email_id"));
				employees.add(employee); // Add the employee to the list
			}
			return employees;
		} catch (SQLException e) {
			throw new CustomeException("Error fetching all employees: " + e.getMessage(), e); // Custom exception handling
		} catch (Exception e) {
			throw new CustomeException("Unexpected error occurred while fetching all employees: " + e.getMessage(), e);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					throw new CustomeException("Failed to close ResultSet", e);
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					throw new CustomeException("Failed to close PreparedStatement", e);
				}
			}

		}
	}

	@Override
	public boolean updateEmployee(EmployeeDTO employee) throws CustomeException {
		String query = "UPDATE employee SET employee_name = ?, department_id = ?, employee_qualification = ?, phone_number = ?, marital_status = ?, gender = ?, email_id = ? WHERE employee_id = ?";
		PreparedStatement stmt = null;

		try {
			stmt = jdbcConnection.getConnection().prepareStatement(query);
			stmt.setString(1, employee.getEmployeeName());
			stmt.setInt(2, employee.getDepartmentId());
			stmt.setString(3, employee.getEmployeeQualification());
			stmt.setString(4, employee.getPhoneNumber());
			stmt.setString(5, employee.getMaritalStatus());
			stmt.setString(6, employee.getGender());
			stmt.setString(7, employee.getEmailId());
			stmt.setString(8, employee.getEmployeeId());

			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			throw new CustomeException("Error updating employee: " + e.getMessage(), e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close(); // Ensure closing of the PreparedStatement
				} catch (SQLException e) {
					throw new CustomeException("Failed to close PreparedStatement", e);
				}
			}
		}
	}

	@Override
	public boolean softDeleteEmployee(String employeeId) throws CustomeException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = jdbcConnection.getConnection();
			String sql = "UPDATE employee SET is_delete = 'YES' WHERE employee_id = ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, employeeId);

			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			throw new CustomeException("Error performing soft delete on employee: " + e.getMessage());
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close(); // Ensure closing of the PreparedStatement
				} catch (SQLException e) {
					throw new CustomeException("Failed to close PreparedStatement");
				}
			}

		}
	}

	@Override
	public boolean hardDeleteEmployee(String employeeId) throws CustomeException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = jdbcConnection.getConnection();
			String sql = "DELETE FROM employee WHERE employee_id = ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, employeeId);

			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			throw new CustomeException("Error performing hard delete on employee: " + e.getMessage());
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close(); // Ensure closing of the PreparedStatement
				} catch (SQLException e) {
					throw new CustomeException("Failed to close PreparedStatement", e);
				}
			}

		}
	}

	@Override
	public boolean isDepartmentExists(int departmentId) throws SQLException {
		String query = "SELECT COUNT(*) FROM department WHERE department_id = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = jdbcConnection.getConnection().prepareStatement(query);
			preparedStatement.setInt(1, departmentId);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getInt(1) > 0;
			}
		} finally {
			// Ensure resources are closed in the finally block
			if (resultSet != null) {
				resultSet.close();
			}
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
		return false;
	}



	@Override
	public boolean deleteEmployee(int employeeId) throws CustomeException {
		// TODO Auto-generated method stub
		return false;
	}

	private static boolean isAdminUser(JDBC_Connection jdbcConnection, String username, String password) throws SQLException {
	    String query = "SELECT * FROM employee WHERE employee_id = ? AND password = ? AND is_admin = 1";
	    try (PreparedStatement preparedStatement = jdbcConnection.getConnection().prepareStatement(query)) {
	        preparedStatement.setString(1, username);
	        preparedStatement.setString(2, password);
	        try (ResultSet resultSet = preparedStatement.executeQuery()) {
	            return resultSet.next();
	        }
	    }
	}
}
