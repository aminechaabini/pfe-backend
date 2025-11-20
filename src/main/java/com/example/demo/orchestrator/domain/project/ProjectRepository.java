package com.example.demo.orchestrator.domain.project;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Project aggregate root.
 *
 * This is a domain interface (port in hexagonal architecture).
 * Infrastructure will provide the implementation (adapter).
 *
 * Works with domain objects only - no knowledge of persistence details.
 */
public interface ProjectRepository {

    /**
     * Save a project (create or update).
     *
     * @param project the project to save
     * @return the saved project with ID assigned
     */
    Project save(Project project);

    /**
     * Find a project by its ID.
     *
     * @param id the project ID
     * @return Optional containing the project if found
     */
    Optional<Project> findById(Long id);

    /**
     * Find a project by its exact name.
     *
     * @param name the project name
     * @return Optional containing the project if found
     */
    Optional<Project> findByName(String name);

    /**
     * Find all projects.
     *
     * @return list of all projects
     */
    List<Project> findAll();

    /**
     * Find all projects ordered by creation date (newest first).
     *
     * @return list of projects ordered by creation date descending
     */
    List<Project> findAllOrderByCreatedAtDesc();

    /**
     * Search projects by name or description (case-insensitive).
     *
     * @param searchTerm the search term
     * @return list of matching projects
     */
    List<Project> search(String searchTerm);

    /**
     * Check if a project with the given name already exists.
     *
     * @param name the project name to check
     * @return true if a project with this name exists
     */
    boolean existsByName(String name);

    /**
     * Delete a project by ID.
     *
     * @param id the project ID
     */
    void deleteById(Long id);

    /**
     * Count total number of projects.
     *
     * @return total count of projects
     */
    long count();

    /**
     * Find a project by ID and eagerly load its test suites.
     *
     * @param id the project ID
     * @return Optional containing the project with test suites loaded
     */
    Optional<Project> findByIdWithTestSuites(Long id);

    /**
     * Find a project by ID and eagerly load its spec sources.
     *
     * @param id the project ID
     * @return Optional containing the project with spec sources loaded
     */
    Optional<Project> findByIdWithSpecSources(Long id);

    /**
     * Find a project by ID and eagerly load its endpoints.
     *
     * @param id the project ID
     * @return Optional containing the project with endpoints loaded
     */
    Optional<Project> findByIdWithEndpoints(Long id);

    /**
     * Find the project that owns a specific test suite.
     *
     * @param testSuiteId the test suite ID
     * @return Optional containing the owning project
     */
    Optional<Project> findByTestSuiteId(Long testSuiteId);

    /**
     * Find the project that owns a specific spec source.
     *
     * @param specSourceId the spec source ID
     * @return Optional containing the owning project
     */
    Optional<Project> findBySpecSourceId(Long specSourceId);

    /**
     * Find the project that owns a specific endpoint.
     *
     * @param endpointId the endpoint ID
     * @return Optional containing the owning project
     */
    Optional<Project> findByEndpointId(Long endpointId);
}
