package com.example.demo.core.application.service;

import com.example.demo.core.application.dto.spec.UploadSpecRequest;
import com.example.demo.core.domain.project.Project;
import com.example.demo.core.domain.project.ProjectRepository;
import com.example.demo.core.domain.spec.Endpoint;
import com.example.demo.core.domain.spec.EndpointRepository;
import com.example.demo.core.domain.spec.SpecSource;
import com.example.demo.core.domain.spec.SpecSourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service for managing API specifications.
 *
 * Responsibilities:
 * - Upload and parse API specs (OpenAPI, Swagger, WSDL)
 * - Extract endpoints from specs
 * - Manage spec sources and their endpoints
 *
 * Uses domain repository interfaces (ports) - infrastructure provides implementations.
 */
@Service
@Transactional
public class SpecSourceService {

    private final SpecSourceRepository specSourceRepository;
    private final EndpointRepository endpointRepository;
    private final ProjectRepository projectRepository;
    // TODO: Inject SpecParser once it's properly implemented
    // private final SpecParser specParser;

    public SpecSourceService(
            SpecSourceRepository specSourceRepository,
            EndpointRepository endpointRepository,
            ProjectRepository projectRepository) {
        this.specSourceRepository = specSourceRepository;
        this.endpointRepository = endpointRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Upload an API specification and extract endpoints.
     *
     * @param request spec upload data
     * @return created spec source with extracted endpoints
     * @throws IllegalArgumentException if project not found or spec name exists
     */
    public SpecSource uploadSpec(UploadSpecRequest request) {
        // Verify project exists
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Project not found: " + request.projectId()));

        // Check for duplicate spec name within project
        if (specSourceRepository.existsByProjectIdAndName(request.projectId(), request.name())) {
            throw new IllegalArgumentException(
                    "Spec source with name '" + request.name() + "' already exists in project");
        }

        // Create spec source domain object
        SpecSource specSource = SpecSource.create(
                request.name(),
                request.fileName(),
                request.specType(),
                request.content()
        );

        // Save spec source
        specSource = specSourceRepository.save(specSource);

        // Add spec source to project
        project.addSpecSource(specSource);
        projectRepository.save(project);

        // TODO: Parse spec and extract endpoints
        // ParsedSpec parsed = specParser.parse(request.content());
        // specSource.setVersion(parsed.getVersion());
        // for (ParsedEndpoint parsedEndpoint : parsed.getEndpoints()) {
        //     Endpoint endpoint = convertToEndpoint(parsedEndpoint, specSource);
        //     specSource.addEndpoint(endpoint);
        //     project.addEndpoint(endpoint);
        // }
        // specSourceRepository.save(specSource);
        // projectRepository.save(project);

        return specSource;
    }

    /**
     * Get a spec source by ID.
     *
     * @param id spec source ID
     * @return spec source
     * @throws IllegalArgumentException if not found
     */
    @Transactional(readOnly = true)
    public SpecSource getSpecSource(Long id) {
        return specSourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Spec source not found: " + id));
    }

    /**
     * Get a spec source with all its endpoints loaded.
     *
     * @param id spec source ID
     * @return spec source with endpoints
     * @throws IllegalArgumentException if not found
     */
    @Transactional(readOnly = true)
    public SpecSource getSpecSourceWithEndpoints(Long id) {
        return specSourceRepository.findByIdWithEndpoints(id)
                .orElseThrow(() -> new IllegalArgumentException("Spec source not found: " + id));
    }

    /**
     * Get all spec sources for a project.
     *
     * @param projectId project ID
     * @return list of spec sources
     */
    @Transactional(readOnly = true)
    public List<SpecSource> getProjectSpecSources(Long projectId) {
        return specSourceRepository.findByProjectId(projectId);
    }

    /**
     * Delete a spec source.
     * This will cascade delete all endpoints extracted from this spec.
     *
     * @param id spec source ID
     * @throws IllegalArgumentException if not found
     */
    public void deleteSpecSource(Long id) {
        if (!specSourceRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Spec source not found: " + id);
        }
        specSourceRepository.deleteById(id);
    }

    /**
     * Get all endpoints extracted from a specific spec source.
     *
     * @param specSourceId spec source ID
     * @return list of endpoints
     */
    @Transactional(readOnly = true)
    public List<Endpoint> getSpecEndpoints(Long specSourceId) {
        return endpointRepository.findBySpecSourceId(specSourceId);
    }

    /**
     * Get all endpoints across all specs in a project.
     * CRUCIAL for E2E test generation - need to see all available endpoints.
     *
     * @param projectId project ID
     * @return list of all endpoints in the project
     */
    @Transactional(readOnly = true)
    public List<Endpoint> getProjectEndpoints(Long projectId) {
        return endpointRepository.findByProjectId(projectId);
    }
}
