package com.example.demo.orchestrator.infrastructure.persistence.entity.project;

import com.example.demo.orchestrator.infrastructure.persistence.common.BaseEntity;
import com.example.demo.orchestrator.infrastructure.persistence.converter.MapToJsonConverter;
import com.example.demo.orchestrator.infrastructure.persistence.entity.spec.EndpointEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.spec.SpecSourceEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.test.TestSuiteEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistence entity for Project.
 * Represents the top-level container for organizing test suites, spec sources, and endpoints.
 *
 * Relationships:
 * - One-to-Many with TestSuiteEntity (project owns test suites)
 * - One-to-Many with SpecSourceEntity (project owns spec sources)
 * - One-to-Many with EndpointEntity (project owns endpoints)
 *
 * Design Decisions:
 * - Variables stored as JSON for simplicity
 * - Uses One-to-Many to reflect aggregate root ownership from domain model
 * - Cascade ALL with orphan removal ensures test suites, specs, and endpoints are tightly bound to project
 * - When a project is deleted, all its test suites, specs, and endpoints are also deleted
 */
@Entity
@Table(name = "projects")
public class ProjectEntity extends BaseEntity {

    @Column(nullable = false, length = 40)
    private String name;

    @Column(length = 2000)
    private String description;

    /**
     * Project-level variables stored as JSON.
     * These can be used in test execution for parameterization.
     */
    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "TEXT", name = "variables")
    private Map<String, String> variables = new HashMap<>();

    /**
     * One-to-Many relationship with test suites.
     * Project owns test suites - all operations cascade.
     * Orphan removal ensures test suites are deleted when removed from project.
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestSuiteEntity> testSuites = new ArrayList<>();

    /**
     * One-to-Many relationship with spec sources.
     * Project owns spec sources - all operations cascade.
     * Orphan removal ensures spec sources are deleted when removed from project.
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<SpecSourceEntity> specSources = new ArrayList<>();

    /**
     * One-to-Many relationship with endpoints.
     * Project owns endpoints - all operations cascade.
     * This is a flat list of all endpoints across all spec sources for easy querying.
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<EndpointEntity> endpoints = new ArrayList<>();

    // Constructors

    public ProjectEntity() {
    }

    public ProjectEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables != null ? variables : new HashMap<>();
    }

    public List<TestSuiteEntity> getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(List<TestSuiteEntity> testSuites) {
        this.testSuites = testSuites != null ? testSuites : new ArrayList<>();
    }

    public List<SpecSourceEntity> getSpecSources() {
        return specSources;
    }

    public void setSpecSources(List<SpecSourceEntity> specSources) {
        this.specSources = specSources != null ? specSources : new ArrayList<>();
    }

    public List<EndpointEntity> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointEntity> endpoints) {
        this.endpoints = endpoints != null ? endpoints : new ArrayList<>();
    }

    // Helper methods

    /**
     * Adds a test suite to this project.
     * Maintains bidirectional relationship.
     */
    public void addTestSuite(TestSuiteEntity testSuite) {
        if (!this.testSuites.contains(testSuite)) {
            this.testSuites.add(testSuite);
            testSuite.setProject(this);
        }
    }

    /**
     * Removes a test suite from this project.
     * Maintains bidirectional relationship.
     */
    public void removeTestSuite(TestSuiteEntity testSuite) {
        this.testSuites.remove(testSuite);
        testSuite.setProject(null);
    }

    /**
     * Adds a spec source to this project.
     * Maintains bidirectional relationship.
     */
    public void addSpecSource(SpecSourceEntity specSource) {
        if (!this.specSources.contains(specSource)) {
            this.specSources.add(specSource);
            specSource.setProject(this);
        }
    }

    /**
     * Removes a spec source from this project.
     * The spec source will be deleted due to orphan removal.
     */
    public void removeSpecSource(SpecSourceEntity specSource) {
        this.specSources.remove(specSource);
        specSource.setProject(null);
    }

    /**
     * Adds an endpoint to this project.
     * Maintains bidirectional relationship.
     */
    public void addEndpoint(EndpointEntity endpoint) {
        if (!this.endpoints.contains(endpoint)) {
            this.endpoints.add(endpoint);
            endpoint.setProject(this);
        }
    }

    /**
     * Removes an endpoint from this project.
     * The endpoint will be deleted due to orphan removal.
     */
    public void removeEndpoint(EndpointEntity endpoint) {
        this.endpoints.remove(endpoint);
        endpoint.setProject(null);
    }

    @Override
    public String toString() {
        return "ProjectEntity{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", variablesCount=" + variables.size() +
                ", testSuitesCount=" + testSuites.size() +
                ", specSourcesCount=" + specSources.size() +
                ", endpointsCount=" + endpoints.size() +
                '}';
    }
}
