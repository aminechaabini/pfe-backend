package com.example.demo.core.application.service;

import com.example.demo.core.application.dto.project.CreateProjectRequest;
import com.example.demo.core.application.dto.project.SetVariableRequest;
import com.example.demo.core.application.dto.project.UpdateProjectRequest;
import com.example.demo.core.domain.project.Project;
import com.example.demo.core.domain.project.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service for managing projects.
 *
 * Responsibilities:
 * - Project CRUD operations
 * - Variable management
 * - Coordinate project aggregate
 *
 * Uses domain repository interface (port) - infrastructure provides implementation.
 */
@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Create a new project.
     *
     * @param request project creation data
     * @return created project
     * @throws IllegalArgumentException if project name already exists
     */
    public Project createProject(CreateProjectRequest request) {
        // Check for duplicate name
        if (projectRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Project with name '" + request.name() + "' already exists");
        }

        // Create domain object using factory method
        Project project = Project.create(request.name(), request.description());

        // Add initial variables if provided
        if (request.initialVariables() != null) {
            request.initialVariables().forEach(project::setVariable);
        }

        // Save - repository handles entity conversion
        return projectRepository.save(project);
    }

    /**
     * Get a project by ID.
     *
     * @param id project ID
     * @return project
     * @throws IllegalArgumentException if project not found
     */
    @Transactional(readOnly = true)
    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    /**
     * Get all projects ordered by creation date (newest first).
     *
     * @return list of all projects
     */
    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        return projectRepository.findAllOrderByCreatedAtDesc();
    }

    /**
     * Update a project.
     * Only non-null fields are updated.
     *
     * @param id project ID
     * @param request update data
     * @return updated project
     * @throws IllegalArgumentException if project not found
     */
    public Project updateProject(Long id, UpdateProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));

        // Update name if provided
        if (request.name() != null) {
            // Check for duplicate name (excluding current project)
            projectRepository.findByName(request.name())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new IllegalArgumentException(
                                    "Project with name '" + request.name() + "' already exists");
                        }
                    });
            project.rename(request.name());
        }

        // Update description if provided
        if (request.description() != null) {
            project.updateDescription(request.description());
        }

        return projectRepository.save(project);
    }

    /**
     * Delete a project.
     * This will cascade delete all test suites, specs, and endpoints.
     *
     * @param id project ID
     * @throws IllegalArgumentException if project not found
     */
    public void deleteProject(Long id) {
        if (!projectRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Project not found: " + id);
        }
        projectRepository.deleteById(id);
    }

    /**
     * Set or update a project variable.
     *
     * @param id project ID
     * @param request variable data
     * @return updated project
     * @throws IllegalArgumentException if project not found or validation fails
     */
    public Project setVariable(Long id, SetVariableRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));

        // Use domain method - handles validation
        project.setVariable(request.name(), request.value());

        return projectRepository.save(project);
    }

    /**
     * Remove a project variable.
     *
     * @param id project ID
     * @param variableName variable name
     * @return updated project
     * @throws IllegalArgumentException if project or variable not found
     */
    public Project removeVariable(Long id, String variableName) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));

        // Use domain method - handles validation
        project.removeVariable(variableName);

        return projectRepository.save(project);
    }
}
