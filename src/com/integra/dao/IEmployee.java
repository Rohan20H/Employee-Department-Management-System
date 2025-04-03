package com.integra.dao;

import com.integra.dto.EmployeeDTO;
import com.integra.Exception.CustomeException;

import java.sql.SQLException;
import java.util.List;

public interface IEmployee {
	boolean addEmployee(EmployeeDTO employee) throws CustomeException;

	EmployeeDTO getEmployeeById(String employeeId) throws CustomeException;

	List<EmployeeDTO> getAllEmployees() throws CustomeException;

	boolean updateEmployee(EmployeeDTO employee) throws CustomeException;

	boolean deleteEmployee(int employeeId) throws CustomeException;

	boolean isDepartmentExists(int departmentId) throws SQLException, CustomeException;

	boolean softDeleteEmployee(String employeeId) throws CustomeException;

	boolean hardDeleteEmployee(String employeeId) throws CustomeException;


}
