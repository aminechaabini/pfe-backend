package com.example.demo.orchestrator.persistence.entity.test;

import com.example.demo.orchestrator.persistence.common.BaseEntity;
import com.example.demo.orchestrator.persistence.converter.MapToJsonConverter;
import com.example.demo.orchestrator.persistence.entity.project.ProjectEntity;
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
 * - Many-to-Many with ProjectEntity (can be shared across projects)
 * - One-to-Many with TestCaseEntity (owns test cases)
 *
 * Design Decisions:
 * - Variables stored as JSON
 * - Test suites are independent aggregate roots (can exist without a project)
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
     * Reverse side of Many-to-Many relationship with projects.
     */
    @ManyToMany(mappedBy = "testSuites")
    private List<ProjectEntity> projects = new ArrayList<>();

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

    public List<ProjectEntity> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectEntity> projects) {
        this.projects = projects != null ? projects : new ArrayList<>();
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
                ", projectsCount=" + projects.size() +
                '}';
    }
}
