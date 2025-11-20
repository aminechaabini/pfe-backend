package com.example.demo.orchestrator.infrastructure.persistence.entity.test;

import com.example.demo.orchestrator.infrastructure.persistence.common.BaseEntity;
import com.example.demo.orchestrator.infrastructure.persistence.converter.MapToJsonConverter;
import com.example.demo.orchestrator.infrastructure.persistence.entity.project.ProjectEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.spec.EndpointEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistence entity for TestSuite.
 * Represents a collection of related test cases.
 *
 * Relationships:
 * - Many-to-One with ProjectEntity (belongs to exactly one project)
 * - One-to-Many with TestCaseEntity (owns test cases)
 * - Many-to-One with EndpointEntity (optional - associated endpoint)
 *
 * Design Decisions:
 * - Variables stored as JSON
 * - Test suites are tightly bound to projects (cannot exist without a project)
 * - project_id is NOT NULL, enforcing ownership
 * - When project is deleted, test suites are cascade deleted
 * - Test cases have tight ownership - cascade all operations
 */
@Entity
@Table(name = "test_suites")
public class TestSuiteEntity extends BaseEntity {

    @Column(nullable = false, length = 40)
    private String name;

    @Column(length = 2000)
    private String description;

    /**
     * Suite-level variables stored as JSON.
     * Can override project-level variables during test execution.
     */
    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "TEXT", name = "variables")
    private Map<String, String> variables = new HashMap<>();

    /**
     * Many-to-One relationship with project.
     * Each test suite belongs to exactly one project.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    /**
     * Many-to-One relationship with endpoint (optional).
     * Test suite is associated with a specific endpoint it tests.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endpoint_id")
    private EndpointEntity endpoint;

    /**
     * One-to-Many relationship with test cases.
     * Test cases are owned by the suite - all operations cascade.
     * Orphan removal ensures test cases are deleted when removed from suite.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "test_suite_id")
    private List<TestCaseEntity> testCases = new ArrayList<>();

    // Constructors

    public TestSuiteEntity() {
    }

    public TestSuiteEntity(String name, String description) {
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

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public EndpointEntity getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointEntity endpoint) {
        this.endpoint = endpoint;
    }

    public List<TestCaseEntity> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCaseEntity> testCases) {
        this.testCases = testCases != null ? testCases : new ArrayList<>();
    }

    // Helper methods

    /**
     * Adds a test case to this suite.
     */
    public void addTestCase(TestCaseEntity testCase) {
        this.testCases.add(testCase);
    }

    /**
     * Removes a test case from this suite.
     * The test case will be deleted due to orphan removal.
     */
    public void removeTestCase(TestCaseEntity testCase) {
        this.testCases.remove(testCase);
    }

    @Override
    public String toString() {
        return "TestSuiteEntity{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", variablesCount=" + variables.size() +
                ", testCasesCount=" + testCases.size() +
                ", projectId=" + (project != null ? project.getId() : null) +
                ", endpointId=" + (endpoint != null ? endpoint.getId() : null) +
                '}';
    }
}
