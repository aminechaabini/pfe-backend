package com.example.demo.core.presentation.rest.controller;

import com.example.demo.core.presentation.rest.dto.response.project.ProjectResponse;
import com.example.demo.core.presentation.rest.mapper.ProjectResponseMapper;
import com.example.demo.core.application.dto.project.CreateProjectRequest;
import com.example.demo.core.application.dto.project.SetVariableRequest;
import com.example.demo.core.application.dto.project.UpdateProjectRequest;
import com.example.demo.core.application.service.ProjectService;
import com.example.demo.core.domain.project.Project;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for Project management.
 *
 * Base path: /api/projects
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectResponseMapper mapper;

    public ProjectController(ProjectService projectService, ProjectResponseMapper mapper) {
        this.projectService = projectService;
        this.mapper = mapper;
    }

    /**
     * Create a new project.
     *
     * POST /api/projects
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid CreateProjectRequest request) {
        Project project = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseWithCounts(project));
    }

    /**
     * Get all projects.
     *
     * GET /api/projects
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects.stream().map(mapper::toResponseWithCounts).toList());
    }

    /**
     * Get project by ID.
     *
     * GET /api/projects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id) {
        Project project = projectService.getProject(id);
        return ResponseEntity.ok(mapper.toResponseWithCounts(project));
    }

    /**
     * Update project.
     *
     * PUT /api/projects/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProjectRequest request) {
        Project project = projectService.updateProject(id, request);
        return ResponseEntity.ok(mapper.toResponseWithCounts(project));
    }

    /**
     * Delete project.
     *
     * DELETE /api/projects/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Set project variable.
     *
     * POST /api/projects/{id}/variables
     */
    @PostMapping("/{id}/variables")
    public ResponseEntity<ProjectResponse> setVariable(
            @PathVariable Long id,
            @RequestBody @Valid SetVariableRequest request) {
        Project project = projectService.setVariable(id, request);
        return ResponseEntity.ok(mapper.toResponseWithCounts(project));
    }

    /**
     * Remove project variable.
     *
     * DELETE /api/projects/{id}/variables/{name}
     */
    @DeleteMapping("/{id}/variables/{name}")
    public ResponseEntity<ProjectResponse> removeVariable(
            @PathVariable Long id,
            @PathVariable String name) {
        Project project = projectService.removeVariable(id, name);
        return ResponseEntity.ok(mapper.toResponseWithCounts(project));
    }
}
