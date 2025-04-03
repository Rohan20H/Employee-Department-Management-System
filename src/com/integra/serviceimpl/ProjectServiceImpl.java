package com.integra.serviceimpl;

import com.integra.dao.IProject;
import com.integra.dto.ProjectDTO;
import com.integra.Exception.CustomeException;
import com.integra.service.IProjectService;

import java.util.List;

public class ProjectServiceImpl implements IProjectService {
	private IProject projectDAO;

	public ProjectServiceImpl(IProject projectDAO) {
		this.projectDAO = projectDAO;
	}

	@Override
	public boolean addProject(ProjectDTO project) throws CustomeException {
		if (!validateProjectName(project.getProjectName())) {
			throw new CustomeException("Invalid project name.");
		}
		if (!validateComments(project.getComments())) {
			throw new CustomeException("Comment should not exceed 50 characters.");
		}
		return projectDAO.addProject(project);
	}

	@Override
	public ProjectDTO getProjectById(int projectId) throws CustomeException {
		return projectDAO.getProjectById(projectId);
	}

	@Override
	public List<ProjectDTO> getAllProjects() throws CustomeException {
		return projectDAO.getAllProjects();
	}

	@Override
	public boolean updateProject(ProjectDTO project) throws CustomeException {
		if (!validateProjectName(project.getProjectName())) {
			throw new CustomeException("Invalid project name.");
		}
		if (!validateComments(project.getComments())) {
			throw new CustomeException("Comments should not exceed 50 characters.");
		}
		return projectDAO.updateProject(project);
	}

	@Override
	public boolean deleteProject(int projectId, boolean isSoftDelete) throws CustomeException {
		if (isSoftDelete) {
			return projectDAO.softDeleteProject(projectId);
		} else {
			return projectDAO.hardDeleteProject(projectId);
		}
	}

	@Override
	public boolean validateProjectName(String projectName) throws CustomeException {
		return projectName != null && !projectName.trim().isEmpty();
	}

	@Override
	public boolean validateComments(String comments) throws CustomeException {
		return comments != null && comments.length() <= 50;
	}

}
