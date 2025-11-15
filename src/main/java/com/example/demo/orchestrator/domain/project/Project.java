package com.example.demo.orchestrator.domain.project;

import com.example.demo.orchestrator.domain.spec.Endpoint;
import com.example.demo.orchestrator.domain.spec.SpecSource;
import com.example.demo.orchestrator.domain.test.test_suite.TestSuite;

import java.time.Instant;
import java.util.*;

public class Project {

    // Constants for validation
    private static final int MAX_NAME_LENGTH = 40;
    private static final int MAX_DESCRIPTION_LENGTH = 2000;
    private static final int MAX_VARIABLE_NAME_LENGTH = 200;
    private static final int MAX_VARIABLE_VALUE_LENGTH = 2000;

    private Long id;
    private String name;
    private String description;
    private final Map<String, String> variables = new HashMap<>();
    private final List<TestSuite> testSuites = new ArrayList<>();
    private final List<SpecSource> specSources = new ArrayList<>();
    private final List<Endpoint> endpoints = new ArrayList<>();
    private final Instant createdAt;
    private Instant updatedAt;

    private Project(String name, String description) {
        validateName(name);
        this.name = name.trim();
        validateDescription(description);
        this.description = description == null ? "" : description.trim();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Factory method to create a new project.
     */
    public static Project create(String name, String description) {
        return new Project(name, description);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Cannot change ID once set");
        }
        this.id = Objects.requireNonNull(id, "ID cannot be null");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public List<TestSuite> getTestSuites() {
        return Collections.unmodifiableList(testSuites);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Rename the project.
     * @param newName the new name (will be trimmed)
     * @throws IllegalArgumentException if name is invalid
     */
    public void rename(String newName) {
        validateName(newName);
        String trimmed = newName.trim();
        if (trimmed.equals(this.name)) return;
        this.name = trimmed;
        touch();
    }

    /**
     * Update the project description.
     * @param newDescription the new description (will be trimmed)
     * @throws IllegalArgumentException if description is invalid
     */
    public void updateDescription(String newDescription) {
        validateDescription(newDescription);
        String trimmed = newDescription == null ? "" : newDescription.trim();
        if (trimmed.equals(this.description)) return; // FIXED: was comparing to name
        this.description = trimmed;
        touch();
    }

    /**
     * Set or update a project variable.
     * @param name the variable name
     * @param value the variable value
     * @throws IllegalArgumentException if name or value is invalid
     */
    public void setVariable(String name, String value) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Variable name cannot be null or blank");
        }
        if (name.length() > MAX_VARIABLE_NAME_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Variable name must be at most %d characters", MAX_VARIABLE_NAME_LENGTH)
            );
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Variable value cannot be null or blank");
        }
        if (value.length() > MAX_VARIABLE_VALUE_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Variable value must be at most %d characters", MAX_VARIABLE_VALUE_LENGTH)
            );
        }
        this.variables.put(name.trim(), value.trim());
        touch();
    }

    /**
     * Remove a variable by name.
     * @param name the variable name
     * @return true if the variable existed and was removed
     * @throws IllegalArgumentException if variable doesn't exist
     */
    public boolean removeVariable(String name) {
        if (name == null || !variables.containsKey(name)) {
            throw new IllegalArgumentException("Variable does not exist: " + name);
        }
        boolean result = this.variables.remove(name) != null;
        if (result) touch();
        return result;
    }

    /**
     * Add a test suite to the project.
     * @param testSuite the test suite to add
     * @throws IllegalArgumentException if suite is null or already exists
     */
    public void addSuite(TestSuite testSuite) {
        Objects.requireNonNull(testSuite, "Test suite cannot be null");
        
        // Check for duplicates by ID if available
        if (testSuite.getId() != null && findTestSuiteById(testSuite.getId()).isPresent()) {
            throw new IllegalArgumentException("Suite with ID " + testSuite.getId() + " already exists in project");
        }
        
        this.testSuites.add(testSuite);
        touch();
    }

    /**
     * Remove a test suite from the project.
     * @param testSuite the test suite to remove
     * @return true if the suite was removed
     */
    public boolean removeSuite(TestSuite testSuite) {
        Objects.requireNonNull(testSuite, "Test suite cannot be null");
        boolean result = this.testSuites.remove(testSuite);
        if (result) touch();
        return result;
    }

    /**
     * Find a test suite by its ID.
     * @param id the test suite ID
     * @return Optional containing the suite if found
     */
    public Optional<TestSuite> findTestSuiteById(Long id) {
        if (id == null) return Optional.empty();
        return testSuites.stream()
                .filter(suite -> id.equals(suite.getId()))
                .findFirst();
    }

    // ========================================================================
    // AGGREGATE ROOT FACADE METHODS
    // Project is the aggregate root for TestSuites. Modifications to TestSuites
    // should go through these methods to maintain consistency and update timestamps.
    // ========================================================================

    /**
     * Rename a test suite within this project.
     * This maintains the aggregate boundary by keeping Project in control.
     *
     * @param testSuiteId the ID of the test suite to rename
     * @param newName the new name for the test suite
     * @throws IllegalArgumentException if suite not found or name invalid
     */
    public void renameTestSuite(Long testSuiteId, String newName) {
        TestSuite suite = findTestSuiteById(testSuiteId)
            .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + testSuiteId));
        suite.rename(newName);
        touch(); // Project aggregate is modified
    }

    /**
     * Update a test suite's description within this project.
     *
     * @param testSuiteId the ID of the test suite
     * @param newDescription the new description
     * @throws IllegalArgumentException if suite not found or description invalid
     */
    public void updateTestSuiteDescription(Long testSuiteId, String newDescription) {
        TestSuite suite = findTestSuiteById(testSuiteId)
            .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + testSuiteId));
        suite.updateDescription(newDescription);
        touch();
    }

    /**
     * Set a variable on a test suite within this project.
     *
     * @param testSuiteId the ID of the test suite
     * @param variableName the variable name
     * @param variableValue the variable value
     * @throws IllegalArgumentException if suite not found or variable invalid
     */
    public void setTestSuiteVariable(Long testSuiteId, String variableName, String variableValue) {
        TestSuite suite = findTestSuiteById(testSuiteId)
            .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + testSuiteId));
        suite.setVariable(variableName, variableValue);
        touch();
    }

    /**
     * Validate the project name.
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or blank");
        }
        if (name.trim().length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Project name must be at most %d characters", MAX_NAME_LENGTH)
            );
        }
    }

    /**
     * Validate the project description.
     */
    private void validateDescription(String description) {
        if (description == null) return; // null is acceptable
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Project description must be at most %d characters", MAX_DESCRIPTION_LENGTH)
            );
        }
    }

    /**
     * Update the updatedAt timestamp.
     */
    private void touch() {
        this.updatedAt = Instant.now();
    }
}
