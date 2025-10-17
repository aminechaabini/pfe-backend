package project.app;

import project.domain.Project;
import project.domain.TestSuite;
import project.infra.ProjectRepository;

import java.util.Optional;
import java.util.UUID;

public class SuiteService {
    private final ProjectRepository projectRepository;

    public SuiteService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<TestSuite> create(UUID projectId, TestSuite suite) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty()) return Optional.empty();
        Project p = project.get();
        if (suite.getId() == null) suite.setId(UUID.randomUUID());
        p.addTestSuite(suite);
        projectRepository.save(p);
        return Optional.of(suite);
    }

    public Optional<TestSuite> get(UUID projectId, UUID suiteId) {
        return projectRepository.findById(projectId)
                .flatMap(p -> p.getTestSuites().stream().filter(s -> s.getId().equals(suiteId)).findFirst());
    }
}
