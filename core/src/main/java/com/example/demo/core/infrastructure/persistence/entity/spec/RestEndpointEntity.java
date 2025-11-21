package com.example.demo.core.infrastructure.persistence.entity.spec;

import com.example.demo.core.domain.spec.EndpointType;
import com.example.demo.core.domain.spec.HttpMethod;
import jakarta.persistence.*;

/**
 * Persistence entity for REST endpoints.
 * Represents a REST API endpoint (e.g., GET /api/orders/{id}).
 *
 * Specific to REST:
 * - HTTP method (GET, POST, PUT, DELETE, etc.)
 * - Path with optional parameters (/api/orders/{id})
 */
@Entity
@DiscriminatorValue("REST")
public class RestEndpointEntity extends EndpointEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private HttpMethod method;

    @Column(nullable = false, length = 500)
    private String path;

    // Constructors

    public RestEndpointEntity() {
    }

    public RestEndpointEntity(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    // Getters and Setters

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // Implemented abstract methods

    @Override
    public EndpointType getType() {
        return EndpointType.REST;
    }

    @Override
    public String getUniqueKey() {
        return method + ":" + path;
    }

    @Override
    public String toString() {
        return "RestEndpointEntity{" +
                "id=" + getId() +
                ", method=" + method +
                ", path='" + path + '\'' +
                ", summary='" + getSummary() + '\'' +
                ", projectId=" + (getProject() != null ? getProject().getId() : null) +
                '}';
    }
}
