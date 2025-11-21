package com.example.demo.core.domain.spec;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SpecSource aggregate root.
 *
 * This is a domain interface (port in hexagonal architecture).
 * Infrastructure will provide the implementation (adapter).
 *
 * Works with domain objects only - no knowledge of persistence details.
 */
public interface SpecSourceRepository {

    /**
     * Save a spec source (create or update).
     *
     * @param specSource the spec source to save
     * @return the saved spec source with ID assigned
     */
    SpecSource save(SpecSource specSource);

    /**
     * Find a spec source by its ID.
     *
     * @param id the spec source ID
     * @return Optional containing the spec source if found
     */
    Optional<SpecSource> findById(Long id);

    /**
     * Find all spec sources.
     *
     * @return list of all spec sources
     */
    List<SpecSource> findAll();

    /**
     * Find all spec sources belonging to a project.
     *
     * @param projectId the project ID
     * @return list of spec sources in the project
     */
    List<SpecSource> findByProjectId(Long projectId);

    /**
     * Find a spec source by project and name (unique constraint).
     *
     * @param projectId the project ID
     * @param name the spec source name
     * @return Optional containing the spec source if found
     */
    Optional<SpecSource> findByProjectIdAndName(Long projectId, String name);

    /**
     * Check if a spec source with given name exists in a project.
     *
     * @param projectId the project ID
     * @param name the spec source name
     * @return true if a spec source with this name exists in the project
     */
    boolean existsByProjectIdAndName(Long projectId, String name);

    /**
     * Find all spec sources of a specific type.
     *
     * @param specType the spec type
     * @return list of spec sources of the given type
     */
    List<SpecSource> findBySpecType(SpecType specType);

    /**
     * Find spec sources by project and type.
     *
     * @param projectId the project ID
     * @param specType the spec type
     * @return list of spec sources matching project and type
     */
    List<SpecSource> findByProjectIdAndSpecType(Long projectId, SpecType specType);

    /**
     * Find all spec sources ordered by creation date (newest first).
     *
     * @return list of spec sources ordered by creation date descending
     */
    List<SpecSource> findAllOrderByCreatedAtDesc();

    /**
     * Find spec sources by project ordered by creation date.
     *
     * @param projectId the project ID
     * @return list of spec sources ordered by creation date descending
     */
    List<SpecSource> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    /**
     * Search spec sources by name or file name.
     *
     * @param keyword the search keyword
     * @return list of matching spec sources
     */
    List<SpecSource> search(String keyword);

    /**
     * Find spec source with endpoints eagerly loaded.
     *
     * @param id the spec source ID
     * @return Optional containing the spec source with endpoints loaded
     */
    Optional<SpecSource> findByIdWithEndpoints(Long id);

    /**
     * Count spec sources in a project.
     *
     * @param projectId the project ID
     * @return number of spec sources in the project
     */
    long countByProjectId(Long projectId);

    /**
     * Delete a spec source by ID.
     *
     * @param id the spec source ID
     */
    void deleteById(Long id);

    /**
     * Count total number of spec sources.
     *
     * @return total count of spec sources
     */
    long count();
}
