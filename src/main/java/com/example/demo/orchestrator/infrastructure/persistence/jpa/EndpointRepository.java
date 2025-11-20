package com.example.demo.orchestrator.infrastructure.persistence.jpa;

import com.example.demo.orchestrator.domain.spec.HttpMethod;
import com.example.demo.orchestrator.infrastructure.persistence.entity.spec.EndpointEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.spec.RestEndpointEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.spec.SoapEndpointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for EndpointEntity.
 * Provides data access methods for API endpoints (REST and SOAP).
 */
public interface EndpointRepository extends JpaRepository<EndpointEntity, Long> {

    /**
     * Find all endpoints belonging to a project.
     */
    List<EndpointEntity> findByProjectId(Long projectId);

    /**
     * Find all endpoints extracted from a spec source.
     */
    List<EndpointEntity> findBySpecSourceId(Long specSourceId);

    /**
     * Find REST endpoint by method and path.
     */
    @Query("SELECT e FROM RestEndpointEntity e WHERE e.method = :method AND e.path = :path")
    Optional<RestEndpointEntity> findRestEndpoint(@Param("method") HttpMethod method, @Param("path") String path);

    /**
     * Find REST endpoint by method and path in a specific project.
     */
    @Query("SELECT e FROM RestEndpointEntity e WHERE e.project.id = :projectId AND e.method = :method AND e.path = :path")
    Optional<RestEndpointEntity> findRestEndpointByProject(@Param("projectId") Long projectId,
                                                            @Param("method") HttpMethod method,
                                                            @Param("path") String path);

    /**
     * Find SOAP endpoint by service name and operation name.
     */
    @Query("SELECT e FROM SoapEndpointEntity e WHERE e.serviceName = :serviceName AND e.operationName = :operationName")
    Optional<SoapEndpointEntity> findSoapEndpoint(@Param("serviceName") String serviceName,
                                                   @Param("operationName") String operationName);

    /**
     * Find SOAP endpoint by service and operation in a specific project.
     */
    @Query("SELECT e FROM SoapEndpointEntity e WHERE e.project.id = :projectId AND " +
           "e.serviceName = :serviceName AND e.operationName = :operationName")
    Optional<SoapEndpointEntity> findSoapEndpointByProject(@Param("projectId") Long projectId,
                                                            @Param("serviceName") String serviceName,
                                                            @Param("operationName") String operationName);

    /**
     * Find endpoint by unique key in a project (for deduplication).
     * Unique key format: "METHOD:PATH" for REST, "ServiceName:OperationName" for SOAP.
     */
    @Query("SELECT e FROM EndpointEntity e WHERE e.project.id = :projectId AND " +
           "((TYPE(e) = RestEndpointEntity AND CONCAT(CAST(e.method AS string), ':', CAST(e.path AS string)) = :uniqueKey) OR " +
           "(TYPE(e) = SoapEndpointEntity AND CONCAT(e.serviceName, ':', e.operationName) = :uniqueKey))")
    Optional<EndpointEntity> findByProjectIdAndUniqueKey(@Param("projectId") Long projectId,
                                                          @Param("uniqueKey") String uniqueKey);

    /**
     * Find all REST endpoints in a project.
     */
    @Query("SELECT e FROM RestEndpointEntity e WHERE e.project.id = :projectId")
    List<RestEndpointEntity> findRestEndpointsByProjectId(@Param("projectId") Long projectId);

    /**
     * Find all SOAP endpoints in a project.
     */
    @Query("SELECT e FROM SoapEndpointEntity e WHERE e.project.id = :projectId")
    List<SoapEndpointEntity> findSoapEndpointsByProjectId(@Param("projectId") Long projectId);

    /**
     * Find REST endpoints by HTTP method.
     */
    @Query("SELECT e FROM RestEndpointEntity e WHERE e.project.id = :projectId AND e.method = :method")
    List<RestEndpointEntity> findRestEndpointsByMethod(@Param("projectId") Long projectId,
                                                        @Param("method") HttpMethod method);

    /**
     * Count endpoints in a project.
     */
    long countByProjectId(Long projectId);

    /**
     * Count endpoints in a spec source.
     */
    long countBySpecSourceId(Long specSourceId);

    /**
     * Count REST endpoints in a project.
     */
    @Query("SELECT COUNT(e) FROM RestEndpointEntity e WHERE e.project.id = :projectId")
    long countRestEndpointsByProjectId(@Param("projectId") Long projectId);

    /**
     * Count SOAP endpoints in a project.
     */
    @Query("SELECT COUNT(e) FROM SoapEndpointEntity e WHERE e.project.id = :projectId")
    long countSoapEndpointsByProjectId(@Param("projectId") Long projectId);

    /**
     * Search endpoints by summary or operation ID.
     */
    @Query("SELECT e FROM EndpointEntity e WHERE e.project.id = :projectId AND " +
           "(e.summary LIKE %:keyword% OR e.operationId LIKE %:keyword%)")
    List<EndpointEntity> searchByKeyword(@Param("projectId") Long projectId,
                                          @Param("keyword") String keyword);
}
