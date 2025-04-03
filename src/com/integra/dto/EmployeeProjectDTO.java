package com.integra.dto;

public class EmployeeProjectDTO {

	private int employeeId;
    private int projectId;

    
	public int getEmployeeId() {
    	return employeeId;
    	}
    public void setEmployeeId(int employeeId) {
    	this.employeeId = employeeId; 
    	}
    public int getProjectId() {
    	return projectId; 
    	}
    public void setProjectId(int projectId) {
    	this.projectId = projectId; 
    	}

    @Override
    public String toString() {
        return "EmployeeProject [employeeId=" + employeeId + ", projectId=" + projectId + "]";
    }
}
