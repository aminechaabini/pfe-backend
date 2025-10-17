package project.app;

import project.domain.Project;
import project.infra.ProjectRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProjectService {
    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public Project create(Project project) {
        if (project.getId() == null) {
            project.setId(UUID.randomUUID());
        }
        return repository.save(project);
    }

    public Optional<Project> get(UUID id) {
        return repository.findById(id);
    }

    public List<Project> list() {
        return repository.findAll();
    }

    //may be not correct (p)

    public Optional<Project> update(UUID id, Project updatedProject) {
        Optional<Project> project = repository.findById(id);
        project.ifPresent(p -> {
            if (updatedProject.getName() != null) p.setName(updatedProject.getName());
            if (updatedProject.getVariables() != null) p.setVariables(updatedProject.getVariables());
            if (updatedProject.getTestSuites() != null) p.setTestSuites(updatedProject.getTestSuites());
            repository.save(p);
        });
        return project;
    }

    public boolean delete(UUID id) {
        Optional<Project> existing = repository.findById(id);
        if (existing.isEmpty()) return false;
        repository.deleteById(id);

        // probabaly add deleting suites and tests depends on what logic is chosen might be chosen by user
        return true;
    }

}
