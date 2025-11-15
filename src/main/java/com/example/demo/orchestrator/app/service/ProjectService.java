package com.example.demo.orchestrator.app.service;

import com.example.demo.orchestrator.app.mapper.config.CycleAvoidingMappingContext;
import com.example.demo.orchestrator.app.mapper.definition.ProjectMapper;
import com.example.demo.orchestrator.app.mapper.definition.TestSuiteMapper;
import com.example.demo.orchestrator.app.service.exception.DuplicateEntityException;
import com.example.demo.orchestrator.app.service.exception.EntityNotFoundException;
import com.example.demo.orchestrator.app.service.spec.SpecParser;
import com.example.demo.orchestrator.domain.project.Project;
import com.example.demo.orchestrator.domain.test.test_suite.TestSuite;
import com.example.demo.orchestrator.infra.SpecParserFactory;
import com.example.demo.orchestrator.persistence.entity.project.ProjectEntity;
import com.example.demo.orchestrator.persistence.entity.test.TestSuiteEntity;
import com.example.demo.orchestrator.persistence.repository.ProjectRepository;
import com.example.demo.orchestrator.persistence.repository.TestSuiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing projects and test suites (MVP version).
 *
 * Responsibilities:
 * - Project CRUD operations
 * - Test Suite CRUD operations within projects
 *
 * MVP Methods (8 total):
 * - createProject, getProject, getAllProjects, deleteProject
 * - createTestSuite, getTestSuite, getProjectSuites, deleteTestSuite
 */
@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TestSuiteRepository testSuiteRepository;
    private final ProjectMapper projectMapper;
    private final TestSuiteMapper testSuiteMapper;

    public ProjectService(
            ProjectRepository projectRepository,
            TestSuiteRepository testSuiteRepository,
            ProjectMapper projectMapper,
            TestSuiteMapper testSuiteMapper) {
        this.projectRepository = projectRepository;
        this.testSuiteRepository = testSuiteRepository;
        this.projectMapper = projectMapper;
        this.testSuiteMapper = testSuiteMapper;
    }

    // ========================================================================
    // PROJECT OPERATIONS
    // ========================================================================

    /**
     * Create a new project.
     *
     * @param name project name (required, unique)
     * @param description project description (optional)
     * @return created project
     * @throws DuplicateEntityException if project name already exists
     */
    public Project createProject(String name, String description) {
        // Check for duplicate name
        if (projectRepository.existsByName(name)) {
            throw new DuplicateEntityException("Project", "name", name);
        }

        // Create domain object
        Project project = Project.create(name, description);

        // Convert to entity and save
        ProjectEntity entity = projectMapper.toEntity(project, new CycleAvoidingMappingContext());
        ProjectEntity saved = projectRepository.save(entity);

        // Convert back to domain
        return projectMapper.toDomain(saved, new CycleAvoidingMappingContext());
    }

    /**
     * Get a project by ID.
     *
     * @param projectId project ID
     * @return project
     * @throws EntityNotFoundException if project not found
     */
    @Transactional(readOnly = true)
    public Project getProject(Long projectId) {
        ProjectEntity entity = projectRepository.findByIdWithTestSuites(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project", projectId));

        return projectMapper.toDomain(entity, new CycleAvoidingMappingContext());
    }

    /**
     * Get all projects.
     *
     * @return list of all projects (ordered by creation date DESC)
     */
    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        List<ProjectEntity> entities = projectRepository.findAllOrderByCreatedAtDesc();

        return entities.stream()
                .map(entity -> projectMapper.toDomain(entity, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    /**
     * Delete a project.
     * Note: This will cascade delete all test suites and tests in the project.
     *
     * @param projectId project ID
     * @throws EntityNotFoundException if project not found
     */
    public void deleteProject(Long projectId) {
        ProjectEntity entity = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project", projectId));

        projectRepository.delete(entity);
    }

    // ========================================================================
    // TEST SUITE OPERATIONS
    // ========================================================================

    /**
     * Create a test suite in a project.
     *
     * @param projectId project ID
     * @param name suite name (required)
     * @param description suite description (optional)
     * @return created test suite
     * @throws EntityNotFoundException if project not found
     * @throws DuplicateEntityException if suite name already exists in project
     */
    public TestSuite createTestSuite(Long projectId, String name, String description) {
        // Fetch project
        ProjectEntity projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project", projectId));

        // Check for duplicate suite name (optional - can be added if needed)
        // For MVP, we'll allow duplicate names across projects

        // Create domain object
        TestSuite testSuite = new TestSuite(name, description);

        // Convert to entity and save
        TestSuiteEntity entity = testSuiteMapper.toEntity(testSuite, new CycleAvoidingMappingContext());

        // Link to project
        entity.getProjects().add(projectEntity);
        projectEntity.getTestSuites().add(entity);

        TestSuiteEntity saved = testSuiteRepository.save(entity);

        // Convert back to domain
        return testSuiteMapper.toDomain(saved, new CycleAvoidingMappingContext());
    }

    /**
     * Get a test suite by ID.
     *
     * @param suiteId test suite ID
     * @return test suite
     * @throws EntityNotFoundException if suite not found
     */
    @Transactional(readOnly = true)
    public TestSuite getTestSuite(Long suiteId) {
        TestSuiteEntity entity = testSuiteRepository.findByIdWithTestCases(suiteId)
                .orElseThrow(() -> new EntityNotFoundException("TestSuite", suiteId));

        return testSuiteMapper.toDomain(entity, new CycleAvoidingMappingContext());
    }

    /**
     * Get all test suites in a project.
     *
     * @param projectId project ID
     * @return list of test suites in the project
     * @throws EntityNotFoundException if project not found
     */
    @Transactional(readOnly = true)
    public List<TestSuite> getProjectSuites(Long projectId) {
        // Verify project exists
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project", projectId);
        }

        List<TestSuiteEntity> entities = testSuiteRepository.findByProjects_Id(projectId);

        return entities.stream()
                .map(entity -> testSuiteMapper.toDomain(entity, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    /**
     * Delete a test suite.
     * Note: This will cascade delete all tests in the suite.
     *
     * @param suiteId test suite ID
     * @throws EntityNotFoundException if suite not found
     */
    public void deleteTestSuite(Long suiteId) {
        TestSuiteEntity entity = testSuiteRepository.findById(suiteId)
                .orElseThrow(() -> new EntityNotFoundException("TestSuite", suiteId));

        testSuiteRepository.delete(entity);
    }

    public void uploadSpecFile(Long projectId, File file) {
        // 1. Validate project exists
        Optional<ProjectEntity> project = projectRepository.findById(projectId);
        if (project.isEmpty()) throw new EntityNotFoundException("Project", projectId);

        // 2. Check for duplicate spec name
        if specSourceRepository.existsByProjectIdAndName(projectId, name):
        throw DuplicateEntityException

        // 3. Parse spec file (delegate to infrastructure)
        SpecParserFactory factory = new SpecParserFactory();
        SpecParser specParser = factory.get;
        parsedSpec = specParser.parse(content, specType)

        // 4. Create domain entities (domain layer creates itself)
        specSource = SpecSource.create(name, fileName, specType, content)
        specSource.setVersion(parsedSpec.getVersion())

        // 5. Extract endpoints from parsed data
        for each parsedEndpoint in parsedSpec.getEndpoints():

        // Check if endpoint already exists in project (deduplication)
        existingEndpoint = endpointRepository.findByProjectIdAndKey(
                projectId,
                parsedEndpoint.getUniqueKey()
        )

        if existingEndpoint:
        // Endpoint already exists (from another spec)
        // Link it to this spec source
        existingEndpoint.addParsedFromSpec(specSource.getId())
        endpoints.add(existingEndpoint)
          else:
        // Create new endpoint
        endpoint = parsedEndpoint.toDomainEntity()
        endpoint.setParsedFromSpec(specSource.getId())
        endpoints.add(endpoint)

        // 6. Add endpoints to project
        for each endpoint in endpoints:
        project.addEndpoint(endpoint)

        // 7. Add spec source to project
        project.addSpecSource(specSource)

        // 8. Persist everything (transactional)
        projectRepository.save(project)  // Cascades to specs and endpoints

        // 9. Return domain object
        return specSource
    }
}
