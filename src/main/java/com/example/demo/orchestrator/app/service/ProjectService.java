package com.example.demo.orchestrator.app.service;

import org.springframework.stereotype.Service;
import com.example.demo.orchestrator.domain.project.Project;
import com.example.demo.orchestrator.infra.ProjectRepository;
import com.example.demo.orchestrator.dto.project.CreateProjectRequest;
import com.example.demo.orchestrator.dto.project.UpdateProjectRequest;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository repository;


    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public Project create(CreateProjectRequest request) {
        Project project;
        if (request.getDescription() == null){
            project = Project.create(request.getName());
        }
        else {
            project = Project.create(request.getName(), request.getDescription());
            }
        return repository.save(project);

        ///  repo find by id project

       // project. add testcase

          ///      repo save(project)
    }

    public Optional<Project> findById(Long id) {
        return repository.findById(id);
    }

    public List<Project> findAll() {
        return repository.findAll();
    }

    //may be not correct (p)

    public Optional<Project> update(Long id, UpdateProjectRequest updatedProject) {
        Optional<Project> project = repository.findById(id);
        project.ifPresent(p -> {
            if (updatedProject.getName() != null) p.rename(updatedProject.getName());
            if (updatedProject.getDescription() != null) p.changeDescription(updatedProject.getDescription());
            repository.save(p);
        });
        return project;
    }

    public boolean delete(Long id) {
        Optional<Project> existing = repository.findById(id);
        if (existing.isEmpty()) return false;
        repository.deleteById(id);

        // probabaly add deleting suites and tests depends on what logic is chosen might be chosen by user
        return true;
    }

}
