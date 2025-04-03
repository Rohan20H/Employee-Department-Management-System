// DAO Implementation: DepartmentDAOImpl
package com.integra.daoimpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.integra.DB.BaseDAO;
import com.integra.DB.JDBC_Connection;
import com.integra.Exception.CustomeException;
import com.integra.dao.IDepartment;
import com.integra.dto.DepartmentDTO;

public class DepartmentDAOImpl extends BaseDAO implements IDepartment {

	public DepartmentDAOImpl(JDBC_Connection jdbcConnection) {
		super(jdbcConnection);
	}

	@Override

	public boolean addDepartment(DepartmentDTO departmentDTO) throws CustomeException {
		String checkQuery = "SELECT COUNT(*) FROM department WHERE department_id = ?";
		PreparedStatement stmt = null; // Declare PreparedStatement outside try block

		try {
			// Check if department_id already exists
			stmt = jdbcConnection.getConnection().prepareStatement(checkQuery);
			stmt.setInt(1, departmentDTO.getDepartmentId());
			ResultSet rs = stmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				throw new CustomeException("Department ID already exists. Please use a unique Department ID.");
			}

			// Insert the new department if department_id is unique
			String insertQuery = "INSERT INTO department (department_id, department_name, comments) VALUES (?, ?, ?)";
			stmt = jdbcConnection.getConnection().prepareStatement(insertQuery);
			stmt.setInt(1, departmentDTO.getDepartmentId());
			stmt.setString(2, departmentDTO.getDepartmentName());
			stmt.setString(3, departmentDTO.getComments());

			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new CustomeException("Error adding Department: " + e.getMessage(), e);
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
	public boolean updateDepartment(DepartmentDTO department) throws CustomeException {
		String query = "UPDATE department SET department_name = ?, comments = ? WHERE department_id = ?";
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = jdbcConnection.getConnection().prepareStatement(query);
			preparedStatement.setString(1, department.getDepartmentName());
			preparedStatement.setString(2, department.getComments());
			preparedStatement.setInt(3, department.getDepartmentId());
			return preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new CustomeException("Failed to update department", e);
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

	// In DepartmentDAOImpl.java

	// Method to perform Soft Delete (Update is_delete column)
	public boolean softDeleteDepartment(int departmentId) throws CustomeException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = jdbcConnection.getConnection();
			String sql = "UPDATE department SET is_delete = 'YES' WHERE department_id = ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, departmentId);
			int rowsAffected = preparedStatement.executeUpdate();

			return rowsAffected > 0;
		} catch (SQLException e) {
			throw new CustomeException("Error performing soft delete on department: " + e.getMessage());
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

	// Method to perform Hard Delete (Delete record from DB)
	public boolean hardDeleteDepartment(int departmentId) throws CustomeException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = jdbcConnection.getConnection();
			String sql = "DELETE FROM department WHERE department_id = ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, departmentId);
			int rowsAffected = preparedStatement.executeUpdate();

			return rowsAffected > 0;
		} catch (SQLException e) {
			throw new CustomeException("Error performing hard delete on department: " + e.getMessage());
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

	// Method to fetch Department by ID considering the is_delete flag
//In DepartmentDAOImpl.java

	public DepartmentDTO getDepartmentById(int departmentId) throws CustomeException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = jdbcConnection.getConnection();
			// Modified SQL query to fetch only departments that are not soft-deleted
			String sql = "SELECT * FROM department WHERE department_id = ? AND is_delete = 'NO'";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, departmentId);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				// If the department exists and is not deleted, return it
				return new DepartmentDTO(resultSet.getInt("department_id"), resultSet.getString("department_name"),
						resultSet.getString("comments"));
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new CustomeException("Error fetching department by ID: " + e.getMessage());
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

	public List<DepartmentDTO> getAllDepartments() throws CustomeException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = jdbcConnection.getConnection();
			String sql = "SELECT * FROM department WHERE is_delete = 'NO'"; // Ensure fetching only non-deleted
																			// departments
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();

			List<DepartmentDTO> departmentList = new ArrayList<>();
			while (resultSet.next()) {
				DepartmentDTO department = new DepartmentDTO(resultSet.getInt("department_id"),
						resultSet.getString("department_name"), resultSet.getString("comments"));
				departmentList.add(department);
			}
			return departmentList;
		} catch (SQLException e) {
			throw new CustomeException("Error fetching departments: " + e.getMessage());
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
	public boolean isDepartmentExists(int departmentId) throws CustomeException {
		String query = "SELECT COUNT(*) FROM department WHERE department_id = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = jdbcConnection.getConnection().prepareStatement(query);
			preparedStatement.setInt(1, departmentId);
			resultSet = preparedStatement.executeQuery();

			return resultSet.next() && resultSet.getInt(1) > 0;
		} catch (SQLException e) {
			throw new CustomeException("Failed to check if department exists", e);
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

}