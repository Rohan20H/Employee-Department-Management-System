  package com.integra.serviceimpl;

import com.integra.Exception.CustomeException;
import com.integra.dao.IDepartment;
import com.integra.dto.DepartmentDTO;
import com.integra.service.IDepartmentService;

import java.util.List;

public class DepartmentServiceImpl implements IDepartmentService {

	private IDepartment departmentDAO;

	// Constructor injection for IDepartment (DAO)
	public DepartmentServiceImpl(IDepartment departmentDAO) {
		this.departmentDAO = departmentDAO;
	}

	@Override
	public boolean addDepartment(DepartmentDTO department) throws CustomeException {
		if (!validateDepartmentName(department.getDepartmentName())) {
			throw new CustomeException("Invalid department name.");
		}
		if (!validateComments(department.getComments())) {
			throw new CustomeException("Comments should not exceed 30 characters.");
		}
		return departmentDAO.addDepartment(department);
	}

	@Override
	public DepartmentDTO getDepartmentById(int departmentId) throws CustomeException {
		return departmentDAO.getDepartmentById(departmentId);
	}

	@Override
	public List<DepartmentDTO> getAllDepartments() throws CustomeException {
		return departmentDAO.getAllDepartments();
	}

	@Override
	public boolean updateDepartment(DepartmentDTO department) throws CustomeException {
		if (!validateDepartmentName(department.getDepartmentName())) {
			throw new CustomeException("Invalid department name.");
		}
		if (!validateComments(department.getComments())) {
			throw new CustomeException("Comments should not exceed 30 characters.");
		}
		return departmentDAO.updateDepartment(department);
	}

	@Override
	public boolean deleteDepartment(int departmentId, boolean isSoftDelete) throws CustomeException {
		if (!isDepartmentExists(departmentId)) {
			throw new CustomeException("Department not found.");
		}
		if (isSoftDelete) {
			return departmentDAO.softDeleteDepartment(departmentId);
		} else {
			return departmentDAO.hardDeleteDepartment(departmentId);
		}
	}

	// Helper method to validate department name
	@Override
	public boolean validateDepartmentName(String departmentName) throws CustomeException {
		if (departmentName == null || departmentName.trim().isEmpty()) {
			throw new CustomeException("Department name cannot be empty or just spaces.");
		}
		return true;
	}

	// Helper method to validate comments length
	public boolean validateComments(String comments) throws CustomeException {
		return comments != null && comments.length() <= 30;
	}
	// Method to check if the department exists
	public boolean isDepartmentExists(int departmentId) throws CustomeException {
		try {
			return departmentDAO.isDepartmentExists(departmentId);
		} catch (Exception e) {
			throw new CustomeException("Error checking department existence", e);
		}
	}
}
