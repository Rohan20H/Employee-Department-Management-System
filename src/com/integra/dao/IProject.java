package com.integra.dao;

import com.integra.dto.ProjectDTO;
import com.integra.Exception.CustomeException;

import java.util.List;

public interface IProject {
	boolean addProject(ProjectDTO project) throws CustomeException;

	ProjectDTO getProjectById(int projectId) throws CustomeException;

	List<ProjectDTO> getAllProjects() throws CustomeException;

	boolean updateProject(ProjectDTO project) throws CustomeException;

	boolean deleteProject(int projectId) throws CustomeException;

	boolean softDeleteProject(int projectId) throws CustomeException;

	boolean hardDeleteProject(int projectId) throws CustomeException;

	boolean isProjectExists(int projectId) throws CustomeException;
}
