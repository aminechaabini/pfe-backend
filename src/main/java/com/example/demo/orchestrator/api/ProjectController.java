package com.example.demo.orchestrator.api;

import com.example.demo.orchestrator.api.dto.*;
import com.example.demo.orchestrator.application.service.ProjectService;
import com.example.demo.orchestrator.domain.project.Project;
import com.example.demo.orchestrator.domain.test.test_suite.TestSuite;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API for Project and TestSuite management.
 *
 * Endpoints:
 * - POST   /api/projects                    - Create project
 * - GET    /api/projects                    - List all projects
 * - GET    /api/projects/{id}               - Get project by ID
 * - DELETE /api/projects/{id}               - Delete project
 * - POST   /api/projects/{id}/suites        - Create test suite in project
 * - GET    /api/projects/{id}/suites        - List suites in project
 * - GET    /api/suites/{id}                 - Get suite by ID
 * - DELETE /api/suites/{id}                 - Delete suite
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // ========================================================================
    // PROJECT ENDPOINTS
    // ========================================================================

    /**
     * Create a new project.
     *
     * POST /api/projects
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody CreateProjectRequest request) {
        Project project = projectService.createProject(request.name(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(toProjectResponse(project));
    }

    /**
     * Get all projects.
     *
     * GET /api/projects
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        List<ProjectResponse> responses = projects.stream()
                .map(this::toProjectResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get project by ID.
     *
     * GET /api/projects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id) {
        Project project = projectService.getProject(id);
        return ResponseEntity.ok(toProjectResponse(project));
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

    // ========================================================================
    // TEST SUITE ENDPOINTS (nested under project)
    // ========================================================================

    /**
     * Create a test suite in a project.
     *
     * POST /api/projects/{projectId}/suites
     */
    @PostMapping("/{projectId}/suites")
    public ResponseEntity<TestSuiteResponse> createTestSuite(
            @PathVariable Long projectId,
            @RequestBody CreateTestSuiteRequest request) {

        TestSuite suite = projectService.createTestSuite(
                projectId,
                request.name(),
                request.description()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toTestSuiteResponse(suite));
    }

    /**
     * Get all test suites in a project.
     *
     * GET /api/projects/{projectId}/suites
     */
    @GetMapping("/{projectId}/suites")
    public ResponseEntity<List<TestSuiteResponse>> getProjectSuites(@PathVariable Long projectId) {
        List<TestSuite> suites = projectService.getProjectSuites(projectId);
        List<TestSuiteResponse> responses = suites.stream()
                .map(this::toTestSuiteResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ========================================================================
    // SUITE ENDPOINTS (standalone)
    // ========================================================================

    /**
     * Get suite by ID.
     *
     * GET /api/suites/{id}
     */
    @GetMapping("/suites/{id}")
    public ResponseEntity<TestSuiteResponse> getTestSuite(@PathVariable Long id) {
        TestSuite suite = projectService.getTestSuite(id);
        return ResponseEntity.ok(toTestSuiteResponse(suite));
    }

    /**
     * Delete suite.
     *
     * DELETE /api/suites/{id}
     */
    @DeleteMapping("/suites/{id}")
    public ResponseEntity<Void> deleteTestSuite(@PathVariable Long id) {
        projectService.deleteTestSuite(id);
        return ResponseEntity.noContent().build();
    }

    // ========================================================================
    // MAPPERS (Domain -> DTO)
    // ========================================================================

    private ProjectResponse toProjectResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getVariables(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    private TestSuiteResponse toTestSuiteResponse(TestSuite suite) {
        return new TestSuiteResponse(
                suite.getId(),
                suite.getName(),
                suite.getDescription(),
                suite.getVariables(),
                suite.getTestCases().size(),
                suite.getCreatedAt(),
                suite.getUpdatedAt()
        );
    }

    @PostMapping("{id}/spec")
    private void uploadSpecFile(@RequestBody File file){
        // call projectService.uploadSpecFile(id, file)
    }
}
