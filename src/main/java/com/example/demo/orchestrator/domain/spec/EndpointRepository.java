package com.example.demo.orchestrator.domain.spec;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Endpoint entities.
 *
 * This is a domain interface (port in hexagonal architecture).
 * Infrastructure will provide the implementation (adapter).
 *
 * Works with domain objects only - no knowledge of persistence details.
 *
 * Note: Endpoints are child entities of SpecSource aggregate, but we provide
 * a repository for querying endpoints independently.
 */
public interface EndpointRepository {

    /**
     * Save an endpoint (create or update).
     *
     * @param endpoint the endpoint to save
     * @return the saved endpoint with ID assigned
     */
    Endpoint save(Endpoint endpoint);

    /**
     * Find an endpoint by its ID.
     *
     * @param id the endpoint ID
     * @return Optional containing the endpoint if found
     */
    Optional<Endpoint> findById(Long id);

    /**
     * Find all endpoints.
     *
     * @return list of all endpoints
     */
    List<Endpoint> findAll();

    /**
     * Find all endpoints belonging to a project.
     *
     * @param projectId the project ID
     * @return list of endpoints in the project
     */
    List<Endpoint> findByProjectId(Long projectId);

    /**
     * Find all endpoints extracted from a spec source.
     *
     * @param specSourceId the spec source ID
     * @return list of endpoints from the spec source
     */
    List<Endpoint> findBySpecSourceId(Long specSourceId);

    /**
     * Find REST endpoint by method and path in a specific project.
     *
     * @param projectId the project ID
     * @param method the HTTP method
     * @param path the endpoint path
     * @return Optional containing the REST endpoint if found
     */
    Optional<RestEndpoint> findRestEndpointByProject(Long projectId, HttpMethod method, String path);

    /**
     * Find SOAP endpoint by service and operation in a specific project.
     *
     * @param projectId the project ID
     * @param serviceName the service name
     * @param operationName the operation name
     * @return Optional containing the SOAP endpoint if found
     */
    Optional<SoapEndpoint> findSoapEndpointByProject(Long projectId, String serviceName, String operationName);

    /**
     * Find endpoint by unique key in a project (for deduplication).
     * Unique key format: "METHOD:PATH" for REST, "ServiceName:OperationName" for SOAP.
     *
     * @param projectId the project ID
     * @param uniqueKey the unique key
     * @return Optional containing the endpoint if found
     */
    Optional<Endpoint> findByProjectIdAndUniqueKey(Long projectId, String uniqueKey);

    /**
     * Find all REST endpoints in a project.
     *
     * @param projectId the project ID
     * @return list of REST endpoints
     */
    List<RestEndpoint> findRestEndpointsByProjectId(Long projectId);

    /**
     * Find all SOAP endpoints in a project.
     *
     * @param projectId the project ID
     * @return list of SOAP endpoints
     */
    List<SoapEndpoint> findSoapEndpointsByProjectId(Long projectId);

    /**
     * Find REST endpoints by HTTP method.
     *
     * @param projectId the project ID
     * @param method the HTTP method
     * @return list of REST endpoints with the given method
     */
    List<RestEndpoint> findRestEndpointsByMethod(Long projectId, HttpMethod method);

    /**
     * Search endpoints by summary or operation ID.
     *
     * @param projectId the project ID
     * @param keyword the search keyword
     * @return list of matching endpoints
     */
    List<Endpoint> searchByKeyword(Long projectId, String keyword);

    /**
     * Count endpoints in a project.
     *
     * @param projectId the project ID
     * @return number of endpoints in the project
     */
    long countByProjectId(Long projectId);

    /**
     * Count endpoints in a spec source.
     *
     * @param specSourceId the spec source ID
     * @return number of endpoints in the spec source
     */
    long countBySpecSourceId(Long specSourceId);

    /**
     * Count REST endpoints in a project.
     *
     * @param projectId the project ID
     * @return number of REST endpoints
     */
    long countRestEndpointsByProjectId(Long projectId);

    /**
     * Count SOAP endpoints in a project.
     *
     * @param projectId the project ID
     * @return number of SOAP endpoints
     */
    long countSoapEndpointsByProjectId(Long projectId);

    /**
     * Delete an endpoint by ID.
     *
     * @param id the endpoint ID
     */
    void deleteById(Long id);

    /**
     * Count total number of endpoints.
     *
     * @return total count of endpoints
     */
    long count();
}
