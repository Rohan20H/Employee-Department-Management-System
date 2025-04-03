package com.integra.dto;

import java.util.Base64;

public class EmployeeDTO {
	private int slno; // Auto-incremented primary key
	private String employeeId; // Manually entered and unique
	private String employeeName;
	private int departmentId;
	private String employeeQualification;
	private String phoneNumber;
	private String maritalStatus;
	private String gender;
	private String emailId;
	private boolean isAdmin;
	private String password;

	// Constructor, getters, setters

	public EmployeeDTO(int slno, String employeeId, String employeeName, int departmentId, String employeeQualification,
			String phoneNumber, String maritalStatus, String gender, String emailId) {
		this.slno = slno;
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.departmentId = departmentId;
		this.employeeQualification = employeeQualification;
		this.phoneNumber = phoneNumber;
		this.maritalStatus = maritalStatus;
		this.gender = gender;
		this.emailId = emailId;
	}

	public EmployeeDTO(String updateEmpId, String updatedEmpName, int updatedDeptId, String updatedQualification,
			String updatedPhone, String updatedMaritalStatus, String updatedGender, String updatedEmailId) {
		this.employeeId = updateEmpId; // Initializing the 'employeeId' with 'updateEmpId'
		this.employeeName = updatedEmpName; // Initializing the 'employeeName' with 'updatedEmpName'
		this.departmentId = updatedDeptId; // Initializing the 'departmentId' with 'updatedDeptId'
		this.employeeQualification = updatedQualification; // Initializing the 'qualification' with
															// 'updatedQualification'
		this.phoneNumber = updatedPhone; // Initializing the 'phoneNumber' with 'updatedPhone'
		this.maritalStatus = updatedMaritalStatus; // Initializing the 'maritalStatus' with 'updatedMaritalStatus'
		this.gender = updatedGender; // Initializing the 'gender' with 'updatedGender'
		this.emailId = updatedEmailId; // Initializing the 'emailId' with 'updatedEmailId'
	}

//	public EmployeeDTO(int updateEmpId, String updatedEmpName, int updatedDeptId, String updatedQualification,
//			String updatedPhone, String updatedMaritalStatus, String updatedGender, String updatedEmailId) {
//		// TODO Auto-generated constructor stub
//	}

	public EmployeeDTO(String empId, int empDeptId, String empName, String qualification, String phoneNumber,
			String maritalStatus, String gender, String emailId, boolean adminStatus) {
		this.employeeId = empId;
		this.departmentId = empDeptId;
		this.employeeName = empName;
		this.employeeQualification = qualification;
		this.phoneNumber = phoneNumber;
		this.maritalStatus = maritalStatus;
		this.gender = gender;
		this.emailId = emailId;
		this.isAdmin = adminStatus;
	}


	public int getSlno() {
		return slno;
	}

	public void setSlno(int slno) {
		this.slno = slno;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public String getEmployeeQualification() {
		return employeeQualification;
	}

	public void setEmployeeQualification(String employeeQualification) {
		this.employeeQualification = employeeQualification;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		// Store the password in Base64 encoded format
		this.password = encodePassword(password);
	}

	// Utility method to encode password as Base64
	private String encodePassword(String password) {
		if (password == null)
			return null;
		return Base64.getEncoder().encodeToString(password.getBytes());
	}

	// Optional: Utility method to decode the password (for verification)
	public static String decodePassword(String encodedPassword) {
		if (encodedPassword == null)
			return null;
		return new String(Base64.getDecoder().decode(encodedPassword));
	}

}
