
package com.integra.daoimpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.integra.DB.BaseDAO;
import com.integra.DB.JDBC_Connection;
import com.integra.Exception.CustomeException;
import com.integra.dao.IProject;
import com.integra.dto.ProjectDTO;

public class ProjectDAOImpl extends BaseDAO implements IProject {

	public ProjectDAOImpl(JDBC_Connection jdbcConnection) {
		super(jdbcConnection);
	}

	@Override
	public boolean addProject(ProjectDTO projectDTO) throws CustomeException {
		// Query to check if the project_id already exists
		String checkQuery = "SELECT COUNT(*) FROM project WHERE project_id = ?";
		PreparedStatement stmt = null;

		try {
			// Check if project_id already exists
			stmt = jdbcConnection.getConnection().prepareStatement(checkQuery);
			stmt.setInt(1, projectDTO.getProjectId());
			ResultSet rs = stmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				// If project_id already exists, throw an exception
				throw new CustomeException("Project ID already exists. Please use a unique Project ID.");
			}

			// Insert the new project if project_id is unique
			String insertQuery = "INSERT INTO project (project_id, project_name, comments) VALUES (?, ?, ?)";
			stmt = jdbcConnection.getConnection().prepareStatement(insertQuery);
			stmt.setInt(1, projectDTO.getProjectId());
			stmt.setString(2, projectDTO.getProjectName());
			stmt.setString(3, projectDTO.getComments());

			// Execute the insertion query
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0; // Returns true if insertion was successful, otherwise false

		} catch (SQLException e) {
			// Wrap the SQLException into a custom exception and throw it
			throw new CustomeException("Error adding Project: " + e.getMessage(), e);
		} finally {
			// Ensure that the PreparedStatement is closed to avoid resource leaks
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// Handle closing failure and throw a new exception
					throw new CustomeException("Failed to close PreparedStatement", e);
				}
			}
		}
	}

	@Override
	public ProjectDTO getProjectById(int projectId) throws CustomeException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = jdbcConnection.getConnection();

			// Modified SQL query to fetch only projects that are not soft-deleted
			String sql = "SELECT * FROM project WHERE project_id = ? AND is_delete = 'NO'";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, projectId);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				// If the project exists and is not deleted, return it
				return new ProjectDTO(resultSet.getInt("project_id"), resultSet.getString("project_name"),
						resultSet.getString("comments"));
			} else {
				return null; // If no project found or it's marked as deleted
			}
		} catch (SQLException e) {
			throw new CustomeException("Error fetching project by ID: " + e.getMessage(), e);
		} finally {
			// Ensure the ResultSet and PreparedStatement are closed
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
	public List<ProjectDTO> getAllProjects() throws CustomeException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = jdbcConnection.getConnection();
			// SQL query modified to fetch only non-deleted projects
			String sql = "SELECT * FROM project WHERE is_delete = 'NO'"; // Ensure fetching only non-deleted projects
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();

			List<ProjectDTO> projectList = new ArrayList<>();
			while (resultSet.next()) {
				// Creating ProjectDTO objects from the result set
				ProjectDTO project = new ProjectDTO(resultSet.getInt("project_id"), resultSet.getString("project_name"),
						resultSet.getString("comments"));
				projectList.add(project);
			}
			return projectList;
		} catch (SQLException e) {
			throw new CustomeException("Error fetching projects: " + e.getMessage(), e);
		} finally {
			// Ensure closing of ResultSet and PreparedStatement
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
	public boolean updateProject(ProjectDTO project) throws CustomeException {
		String query = "UPDATE project SET project_name = ?, comments = ? WHERE project_id = ?";
		PreparedStatement preparedStatement = null;

		try {
			// Prepare the SQL query for updating the project
			preparedStatement = jdbcConnection.getConnection().prepareStatement(query);

			// Set the values for project name, comments, and project ID
			preparedStatement.setString(1, project.getProjectName());
			preparedStatement.setString(2, project.getComments());
			preparedStatement.setInt(3, project.getProjectId());

			// Execute the update and return true if at least one row is updated
			return preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			// Throw a custom exception with the error message
			throw new CustomeException("Failed to update project", e);
		} finally {
			// Ensure the PreparedStatement is closed after usage
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
	public boolean softDeleteProject(int projectId) throws CustomeException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = jdbcConnection.getConnection();

			// Update the 'project' table, setting 'is_delete' to 'YES' for the given
			// project ID
			String sql = "UPDATE project SET is_delete = 'YES' WHERE project_id = ?";
			preparedStatement = connection.prepareStatement(sql);

			// Set the project ID for the query
			preparedStatement.setInt(1, projectId);

			// Execute the update and check if any rows were affected
			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected > 0; // Return true if a project was soft-deleted
		} catch (SQLException e) {
			// Handle any SQL exceptions by throwing a custom exception
			throw new CustomeException("Error performing soft delete on project: " + e.getMessage(), e);
		} finally {
			// Ensure the PreparedStatement is closed properly in the finally block
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
	public boolean hardDeleteProject(int projectId) throws CustomeException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = jdbcConnection.getConnection();

			// SQL query to permanently delete a project from the 'project' table
			String sql = "DELETE FROM project WHERE project_id = ?";
			preparedStatement = connection.prepareStatement(sql);

			// Set the project ID for the query
			preparedStatement.setInt(1, projectId);

			// Execute the delete query and check if any rows were affected
			int rowsAffected = preparedStatement.executeUpdate();
			return rowsAffected > 0; // Return true if a project was hard-deleted
		} catch (SQLException e) {
			// Handle any SQL exceptions by throwing a custom exception
			throw new CustomeException("Error performing hard delete on project: " + e.getMessage(), e);
		} finally {
			// Ensure the PreparedStatement is closed properly in the finally block
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
	public boolean isProjectExists(int projectId) throws CustomeException {
		String query = "SELECT COUNT(*) FROM project WHERE project_id = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = jdbcConnection.getConnection().prepareStatement(query);
			preparedStatement.setInt(1, projectId);
			resultSet = preparedStatement.executeQuery();

			return resultSet.next() && resultSet.getInt(1) > 0;
		} catch (SQLException e) {
			throw new CustomeException("Failed to check if project exists", e);
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

//    private ProjectDTO mapToProjectDTO(ResultSet resultSet) throws SQLException {
//        int slno = resultSet.getInt("slno");
//        int projectId = resultSet.getInt("project_id");
//        String projectName = resultSet.getString("project_name");
//        String comments = resultSet.getString("comments");
//        String isDelete = resultSet.getString("is_delete");
//
//        return new ProjectDTO(slno, projectId, projectName, comments, isDelete); // Assuming is_delete field exists
//    }

	@Override
	public boolean deleteProject(int projectId) throws CustomeException {
		// TODO Auto-generated method stub
		return false;
	}
}
