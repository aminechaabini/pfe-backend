package com.example.demo.core.infrastructure.persistence.jpa;

import com.example.demo.core.infrastructure.persistence.entity.project.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ProjectEntity - manages project persistence operations.
 * Projects are top-level aggregate roots that organize test suites, spec sources, and endpoints.
 *
 * Key Responsibilities:
 * - CRUD operations for projects
 * - Query projects by name
 * - Search and filter projects
 * - Load projects with relationships (test suites, spec sources, endpoints)
 */

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    /**
     * Finds a project by its exact name.
     * Names should be unique per application design.
     *
     * @param name the project name
     * @return Optional containing the project if found
     */
    Optional<ProjectEntity> findByName(String name);

    /**
     * Searches for projects by name (case-insensitive partial match).
     *
     * @param searchTerm the search term to match against project names
     * @return list of matching projects
     */
    List<ProjectEntity> findByNameContainingIgnoreCase(String searchTerm);

    /**
     * Checks if a project with the given name already exists.
     * Useful for validation before creating new projects.
     *
     * @param name the project name to check
     * @return true if a project with this name exists
     */
    boolean existsByName(String name);

    /**
     * Finds a project by ID and eagerly fetches its test suites.
     * Uses JOIN FETCH to avoid N+1 query problem.
     *
     * @param id the project ID
     * @return Optional containing the project with test suites loaded
     */
    @Query("SELECT p FROM ProjectEntity p LEFT JOIN FETCH p.testSuites WHERE p.id = :id")
    Optional<ProjectEntity> findByIdWithTestSuites(@Param("id") Long id);

    /**
     * Counts the total number of projects in the system.
     *
     * @return total count of projects
     */
    @Query("SELECT COUNT(p) FROM ProjectEntity p")
    long countProjects();

    /**
     * Finds all projects ordered by creation date (newest first).
     *
     * @return list of projects ordered by creation date descending
     */
    @Query("SELECT p FROM ProjectEntity p ORDER BY p.createdAt DESC")
    List<ProjectEntity> findAllOrderByCreatedAtDesc();

    /**
     * Searches projects by name or description (case-insensitive).
     *
     * @param searchTerm the search term
     * @return list of matching projects
     */
    @Query("SELECT p FROM ProjectEntity p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<ProjectEntity> search(@Param("searchTerm") String searchTerm);

    /**
     * Finds the project that owns a specific test suite.
     * Since test suites belong to exactly one project, this returns Optional.
     *
     * @param testSuiteId the test suite ID
     * @return Optional containing the owning project
     */
    @Query("SELECT ts.project FROM TestSuiteEntity ts WHERE ts.id = :testSuiteId")
    Optional<ProjectEntity> findByTestSuiteId(@Param("testSuiteId") Long testSuiteId);

    /**
     * Finds a project by ID and eagerly fetches its spec sources.
     *
     * @param id the project ID
     * @return Optional containing the project with spec sources loaded
     */
    @Query("SELECT p FROM ProjectEntity p LEFT JOIN FETCH p.specSources WHERE p.id = :id")
    Optional<ProjectEntity> findByIdWithSpecSources(@Param("id") Long id);

    /**
     * Finds a project by ID and eagerly fetches its endpoints.
     *
     * @param id the project ID
     * @return Optional containing the project with endpoints loaded
     */
    @Query("SELECT p FROM ProjectEntity p LEFT JOIN FETCH p.endpoints WHERE p.id = :id")
    Optional<ProjectEntity> findByIdWithEndpoints(@Param("id") Long id);

    /**
     * Finds the project that owns a specific spec source.
     *
     * @param specSourceId the spec source ID
     * @return Optional containing the owning project
     */
    @Query("SELECT ss.project FROM SpecSourceEntity ss WHERE ss.id = :specSourceId")
    Optional<ProjectEntity> findBySpecSourceId(@Param("specSourceId") Long specSourceId);

    /**
     * Finds the project that owns a specific endpoint.
     *
     * @param endpointId the endpoint ID
     * @return Optional containing the owning project
     */
    @Query("SELECT e.project FROM EndpointEntity e WHERE e.id = :endpointId")
    Optional<ProjectEntity> findByEndpointId(@Param("endpointId") Long endpointId);
}
