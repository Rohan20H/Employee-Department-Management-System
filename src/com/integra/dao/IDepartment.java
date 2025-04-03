package com.integra.dao;

import java.sql.SQLException;
import java.util.List;
import com.integra.Exception.CustomeException;
import com.integra.dto.DepartmentDTO;

public interface IDepartment {
	boolean addDepartment(DepartmentDTO department) throws CustomeException;

	DepartmentDTO getDepartmentById(int departmentId) throws CustomeException;

	List<DepartmentDTO> getAllDepartments() throws CustomeException;

	boolean updateDepartment(DepartmentDTO department) throws CustomeException;

	boolean isDepartmentExists(int departmentId) throws SQLException, CustomeException;

	boolean softDeleteDepartment(int departmentId) throws CustomeException;

	boolean hardDeleteDepartment(int departmentId) throws CustomeException;

}
