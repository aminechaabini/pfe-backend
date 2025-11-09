package com.example.demo.orchestrator.persistence.entity.project;

import com.example.demo.orchestrator.persistence.common.BaseEntity;
import com.example.demo.orchestrator.persistence.converter.MapToJsonConverter;
import com.example.demo.orchestrator.persistence.entity.test.TestSuiteEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistence entity for Project.
 * Represents the top-level container for organizing test suites.
 *
 * Relationships:
 * - Many-to-Many with TestSuiteEntity (test suites can be shared across projects)
 *
 * Design Decisions:
 * - Variables stored as JSON for simplicity
 * - Uses Many-to-Many to enable test suite reusability
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
     * Many-to-Many relationship with test suites.
     * Test suites can be shared and reused across multiple projects.
     * Join table: project_test_suites
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "project_test_suites",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "test_suite_id")
    )
    private List<TestSuiteEntity> testSuites = new ArrayList<>();

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

    // Helper methods

    /**
     * Adds a test suite to this project.
     * Maintains bidirectional relationship.
     */
    public void addTestSuite(TestSuiteEntity testSuite) {
        if (!this.testSuites.contains(testSuite)) {
            this.testSuites.add(testSuite);
            testSuite.getProjects().add(this);
        }
    }

    /**
     * Removes a test suite from this project.
     * Maintains bidirectional relationship.
     */
    public void removeTestSuite(TestSuiteEntity testSuite) {
        this.testSuites.remove(testSuite);
        testSuite.getProjects().remove(this);
    }

    @Override
    public String toString() {
        return "ProjectEntity{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", variablesCount=" + variables.size() +
                ", testSuitesCount=" + testSuites.size() +
                '}';
    }
}
