package com.integra.dto;

public class ProjectDTO {
	private int slno; // Auto-incremented field, generated by the database
	private int projectId; // User-defined field for project_id
	private String projectName; // Name of the project
	private String comments; // Comments related to the project
	private String isDelete;

	// Constructor for adding a new project (with projectId and comments, but slno
	// is auto-generated)
	public ProjectDTO(int projectId, String projectName, String comments) {
		this.projectId = projectId;
		this.projectName = projectName;
		this.comments = comments;
	}

	// Constructor for updating an existing project (with slno, projectId, and
	// comments)
	public ProjectDTO(int slno, int projectId, String projectName, String comments) {
		this.slno = slno;
		this.projectId = projectId;
		this.projectName = projectName;
		this.comments = comments;
	}

	public ProjectDTO(int slno2, int projectId2, String projectName2, String comments2, String isDelete) {
		this.slno = slno2;
		this.projectId = projectId2;
		this.projectName = projectName2;
		this.comments = comments2;
		this.setIsDelete(isDelete);
	}

	// Getter and Setter for slno
	public int getSlno() {
		return slno;
	}

	public void setSlno(int slno) {
		this.slno = slno;
	}

	// Getter and Setter for projectId
	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	// Getter and Setter for projectName
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	// Getter and Setter for comments
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "ProjectDTO{" + "slno=" + slno + ", projectId=" + projectId + ", projectName='" + projectName + '\''
				+ ", comments='" + comments + '\'' + '}';
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
}
