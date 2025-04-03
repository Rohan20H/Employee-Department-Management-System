package com.integra.service;

import com.integra.dto.ProjectDTO;
import com.integra.Exception.CustomeException;

import java.util.List;

public interface IProjectService {

	boolean addProject(ProjectDTO project) throws CustomeException;

	ProjectDTO getProjectById(int projectId) throws CustomeException;

	List<ProjectDTO> getAllProjects() throws CustomeException;

	boolean updateProject(ProjectDTO project) throws CustomeException;

	boolean deleteProject(int projectId, boolean isSoftDelete) throws CustomeException;

	boolean validateProjectName(String projectName) throws CustomeException;

	boolean validateComments(String comments) throws CustomeException;

}
