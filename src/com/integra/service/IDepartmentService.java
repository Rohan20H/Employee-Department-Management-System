// Service Interface: IDepartmentService
package com.integra.service;

import java.util.List;
import com.integra.Exception.CustomeException;
import com.integra.dto.DepartmentDTO;

public interface IDepartmentService {
	boolean addDepartment(DepartmentDTO department) throws CustomeException;

	DepartmentDTO getDepartmentById(int departmentId) throws CustomeException;

	List<DepartmentDTO> getAllDepartments() throws CustomeException;

	boolean updateDepartment(DepartmentDTO department) throws CustomeException;

	boolean validateDepartmentName(String departmentName) throws CustomeException;

	boolean validateComments(String comments) throws CustomeException;

	boolean isDepartmentExists(int departmentId) throws CustomeException;

	boolean deleteDepartment(int departmentId, boolean isSoftDelete) throws CustomeException;
}