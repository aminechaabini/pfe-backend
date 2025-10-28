
package com.example.demo.orchestrator.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

import com.example.demo.orchestrator.app.mapper.project.ProjectMapper;
import com.example.demo.orchestrator.app.service.ProjectService;
import com.example.demo.orchestrator.domain.project.Project;
import com.example.demo.orchestrator.dto.project.CreateProjectRequest;
import com.example.demo.orchestrator.dto.project.UpdateProjectRequest;
import com.example.demo.orchestrator.dto.project.ProjectResponse;


@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    ProjectController(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

    @PostMapping()
    public ResponseEntity<ProjectResponse> create(@RequestBody CreateProjectRequest request) {
        return ResponseEntity.ok(projectMapper.toResponse(projectService.create(request)));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> findById(@PathVariable("projectId") Long projectId) {
        Optional<Project> project = projectService.findById(projectId);
        if (project.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        else return ResponseEntity.ok(projectMapper.toResponse(project.get()));
    }

    @GetMapping()
    public ResponseEntity<List<ProjectResponse>> findAll() {
        List<Project> projects = projectService.findAll();
        List<ProjectResponse> responses = projects.stream()
                .map(projectMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> update(@PathVariable("projectId") Long projectId, @RequestBody UpdateProjectRequest request) {
        return ResponseEntity.ok(projectMapper.toResponse(projectService.update(projectId, request).get()));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete(@PathVariable("projectId") Long projectId) {
        if(projectService.delete(projectId) == false){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        else return ResponseEntity.ok(null);
    }
}