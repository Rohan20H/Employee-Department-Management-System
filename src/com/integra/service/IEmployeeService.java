package com.integra.service;

import com.integra.dto.EmployeeDTO;
import com.integra.Exception.CustomeException;

import java.util.List;

public interface IEmployeeService {
	boolean addEmployee(EmployeeDTO employee) throws CustomeException;

	EmployeeDTO getEmployeeById(String empIdForView) throws CustomeException;

	List<EmployeeDTO> getAllEmployees() throws CustomeException;

	boolean updateEmployee(EmployeeDTO employee) throws CustomeException;

	boolean deleteEmployee(String employeeId, boolean isSoftDelete) throws CustomeException;

}