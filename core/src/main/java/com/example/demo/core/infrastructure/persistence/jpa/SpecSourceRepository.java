package com.example.demo.core.infrastructure.persistence.jpa;

import com.example.demo.core.domain.spec.SpecType;
import com.example.demo.core.infrastructure.persistence.entity.spec.SpecSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for SpecSourceEntity.
 * Provides data access methods for specification sources.
 */
public interface SpecSourceRepository extends JpaRepository<SpecSourceEntity, Long> {

    /**
     * Find all spec sources belonging to a project.
     */
    List<SpecSourceEntity> findByProjectId(Long projectId);

    /**
     * Find a spec source by project and name (unique constraint).
     */
    Optional<SpecSourceEntity> findByProjectIdAndName(Long projectId, String name);

    /**
     * Check if a spec source with given name exists in a project.
     */
    boolean existsByProjectIdAndName(Long projectId, String name);

    /**
     * Find all spec sources of a specific type.
     */
    List<SpecSourceEntity> findBySpecType(SpecType specType);

    /**
     * Find spec sources by project and type.
     */
    List<SpecSourceEntity> findByProjectIdAndSpecType(Long projectId, SpecType specType);

    /**
     * Find spec source with endpoints eagerly loaded.
     */
    @Query("SELECT ss FROM SpecSourceEntity ss LEFT JOIN FETCH ss.endpoints WHERE ss.id = :id")
    Optional<SpecSourceEntity> findByIdWithEndpoints(@Param("id") Long id);

    /**
     * Count spec sources in a project.
     */
    long countByProjectId(Long projectId);

    /**
     * Search spec sources by name or file name.
     */
    @Query("SELECT ss FROM SpecSourceEntity ss WHERE " +
           "ss.name LIKE %:keyword% OR ss.fileName LIKE %:keyword%")
    List<SpecSourceEntity> search(@Param("keyword") String keyword);

    /**
     * Find all spec sources ordered by creation date (newest first).
     */
    @Query("SELECT ss FROM SpecSourceEntity ss ORDER BY ss.createdAt DESC")
    List<SpecSourceEntity> findAllOrderByCreatedAtDesc();

    /**
     * Find spec sources by project ordered by creation date.
     */
    @Query("SELECT ss FROM SpecSourceEntity ss WHERE ss.project.id = :projectId ORDER BY ss.createdAt DESC")
    List<SpecSourceEntity> findByProjectIdOrderByCreatedAtDesc(@Param("projectId") Long projectId);
}
