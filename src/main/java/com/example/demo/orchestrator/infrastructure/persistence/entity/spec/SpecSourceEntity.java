package com.example.demo.orchestrator.infrastructure.persistence.entity.spec;

import com.example.demo.orchestrator.domain.spec.SpecType;
import com.example.demo.orchestrator.infrastructure.persistence.common.BaseEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.project.ProjectEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistence entity for SpecSource.
 * Represents an API specification source (OpenAPI, Swagger, WSDL).
 *
 * Relationships:
 * - Many-to-One with ProjectEntity (belongs to one project)
 * - One-to-Many with EndpointEntity (owns endpoints extracted from spec)
 *
 * Design Decisions:
 * - Content stored as TEXT for large spec files
 * - Spec content is immutable once created
 * - Name must be unique within a project
 * - Cascade ALL to endpoints (when spec deleted, endpoints deleted)
 */
@Entity
@Table(name = "spec_sources",
       uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "name"}))
public class SpecSourceEntity extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "spec_type", nullable = false, length = 20)
    private SpecType specType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 50)
    private String version;

    /**
     * Many-to-One relationship with project.
     * Each spec source belongs to exactly one project.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    /**
     * One-to-Many relationship with endpoints.
     * Spec source owns all endpoints extracted from it.
     * All operations cascade, orphan removal enabled.
     */
    @OneToMany(mappedBy = "specSource", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<EndpointEntity> endpoints = new ArrayList<>();

    // Constructors

    public SpecSourceEntity() {
    }

    public SpecSourceEntity(String name, String fileName, SpecType specType, String content) {
        this.name = name;
        this.fileName = fileName;
        this.specType = specType;
        this.content = content;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public SpecType getSpecType() {
        return specType;
    }

    public void setSpecType(SpecType specType) {
        this.specType = specType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public List<EndpointEntity> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointEntity> endpoints) {
        this.endpoints = endpoints != null ? endpoints : new ArrayList<>();
    }

    // Helper methods

    /**
     * Adds an endpoint to this spec source.
     * Maintains bidirectional relationship.
     */
    public void addEndpoint(EndpointEntity endpoint) {
        if (!this.endpoints.contains(endpoint)) {
            this.endpoints.add(endpoint);
            endpoint.setSpecSource(this);
        }
    }

    /**
     * Removes an endpoint from this spec source.
     * The endpoint will be deleted due to orphan removal.
     */
    public void removeEndpoint(EndpointEntity endpoint) {
        this.endpoints.remove(endpoint);
        endpoint.setSpecSource(null);
    }

    @Override
    public String toString() {
        return "SpecSourceEntity{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                ", specType=" + specType +
                ", version='" + version + '\'' +
                ", projectId=" + (project != null ? project.getId() : null) +
                ", endpointsCount=" + endpoints.size() +
                '}';
    }
}
