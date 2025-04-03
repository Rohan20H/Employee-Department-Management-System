package com.integra.dto;

public class DepartmentDTO {
	private int slno; // Renamed department_id from previous version
	private int departmentId; // New department_id field from later version
	private String departmentName;
	private String comments;
	private String isDelete; // Add isDelete field for tracking soft delete status

	// Constructor with all fields
	public DepartmentDTO(int slno, int departmentId, String departmentName, String comments, String isDelete) {
		this.slno = slno;
		this.departmentId = departmentId;
		this.departmentName = departmentName;
		this.comments = comments;
		this.isDelete = isDelete;
	}

	// Constructor without isDelete field
	public DepartmentDTO(int departmentId, String departmentName, String comments) {
		this.departmentId = departmentId;
		this.departmentName = departmentName;
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "DepartmentDTO [slno=" + slno + ", departmentId=" + departmentId + ", departmentName=" + departmentName
				+ ", comments=" + comments + ", isDelete=" + isDelete + "]";
	}

	// Constructor for new department (without slno and isDelete)
	public DepartmentDTO(String departmentName, String comments) {
		this.departmentName = departmentName;
		this.comments = comments;
	}

	public DepartmentDTO(int departmentId2, String departmentName2, String comments2, String isDelete2) {
		this.departmentId = departmentId2;
		this.departmentName = departmentName2;
		this.comments = comments2;
		this.isDelete = isDelete2;
	}

	// Getters and Setters
	public int getSlno() {
		return slno;
	}

	public void setSlno(int slno) {
		this.slno = slno;
	}

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		if (comments != null && comments.length() <= 30) {
			this.comments = comments;
		} else {
			throw new IllegalArgumentException("Comments cannot exceed 30 characters.");
		}
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
}
