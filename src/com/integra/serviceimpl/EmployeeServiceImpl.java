package com.integra.serviceimpl;

import com.integra.Exception.CustomeException;
import com.integra.dao.IEmployee;
import com.integra.dto.EmployeeDTO;
import com.integra.service.IEmployeeService;

import java.sql.SQLException;
import java.util.List;

public class EmployeeServiceImpl implements IEmployeeService {

	private IEmployee employeeDAO;

	public EmployeeServiceImpl(IEmployee employeeDAO) {
		this.employeeDAO = employeeDAO;
	}

	@Override
	public boolean addEmployee(EmployeeDTO employee) throws CustomeException {
		// Validate employee data
		if (!validateEmployee(employee)) {
			throw new CustomeException("Validation failed. Employee data is incorrect.");
		}
		return employeeDAO.addEmployee(employee); // Proceed if validation passed
	}

	@Override
	public EmployeeDTO getEmployeeById(String employeeId) throws CustomeException {
		return employeeDAO.getEmployeeById(employeeId);
	}

	@Override
	public List<EmployeeDTO> getAllEmployees() throws CustomeException {
		return employeeDAO.getAllEmployees();
	}

	@Override
	public boolean updateEmployee(EmployeeDTO employee) throws CustomeException {
		// Validate employee data
		if (!validateEmployee(employee)) {
			throw new CustomeException("Validation failed. Employee data is incorrect.");
		}
		return employeeDAO.updateEmployee(employee); // Proceed if validation passed
	}

	@Override
	public boolean deleteEmployee(String employeeId, boolean isSoftDelete) throws CustomeException {

		if (isSoftDelete) {
			return employeeDAO.softDeleteEmployee(employeeId);
		} else {
			return employeeDAO.hardDeleteEmployee(employeeId);
		}
	}

	private boolean validateEmployee(EmployeeDTO employee) throws CustomeException {
		try {
			// Validate department existence
			if (!employeeDAO.isDepartmentExists(employee.getDepartmentId())) {
				throw new CustomeException("Error: Department ID does not exist.");
			}

			// Validate employee name
			if (employee.getEmployeeName() == null || employee.getEmployeeName().trim().isEmpty()) {
				throw new CustomeException("Error: Employee name cannot be empty.");
			}

			// Validate phone number (should be exactly 10 digits)
			if (employee.getPhoneNumber() == null || !employee.getPhoneNumber().matches("\\d{10}")) {
				throw new CustomeException("Error: Phone number must be exactly 10 digits.");
			}

			// Validate email format (must end with @gmail.com)
			if (employee.getEmailId() == null || !employee.getEmailId().matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {//regurler exp
				throw new CustomeException("Error: Email must end with @gmail.com.");
			}

			// Validate marital status (must be "Married", "Single", "Divorced", or "Widowed")
			if (employee.getMaritalStatus() == null || 
			    (!employee.getMaritalStatus().equalsIgnoreCase("Married")
			    && !employee.getMaritalStatus().equalsIgnoreCase("Single")
			    && !employee.getMaritalStatus().equalsIgnoreCase("Divorced")
			    && !employee.getMaritalStatus().equalsIgnoreCase("Widowd"))) {
			    throw new CustomeException("Error: Marital status must be either 'Married', 'Single', 'Divorced', or 'Widowd'.");
			}


			// Validate gender (must be "Male", "Female", or "Other")
			if (employee.getGender() == null || 
			    (!employee.getGender().equalsIgnoreCase("Male") 
			    && !employee.getGender().equalsIgnoreCase("Female") 
			    && !employee.getGender().equalsIgnoreCase("Other"))) {
			    throw new CustomeException("Error: Gender must be either 'Male', 'Female', or 'Other'.");
			}

			
			// Validate qualification (must be one of the valid options)
	        if (!isValidQualification(employee.getEmployeeQualification())) {
	            throw new CustomeException("Error: Invalid qualification. It must be one of 'BE', 'BTech', 'BSC', 'BCA', or 'MCA'.");
	        }

		} catch (SQLException e) {
			throw new CustomeException("Error during employee validation", e);
		}
		return true;
	}

	private static boolean isValidQualification(String qualification) {
	    return qualification != null && (qualification.equalsIgnoreCase("BE") || qualification.equalsIgnoreCase("BTech") 
	            || qualification.equalsIgnoreCase("BSC") || qualification.equalsIgnoreCase("BCA") || qualification.equalsIgnoreCase("MCA"));
	}
}
