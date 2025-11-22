package com.example.demo.core.domain.project;

import com.example.demo.core.domain.spec.Endpoint;
import com.example.demo.core.domain.spec.SpecSource;
import com.example.demo.core.domain.test.test_suite.TestSuite;

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

    /**
     * Reconstitute project from persistence (use in mappers only).
     * Bypasses validation since data is already persisted.
     */
    public static Project reconstitute(
            Long id,
            String name,
            String description,
            Map<String, String> variables,
            Instant createdAt,
            Instant updatedAt) {

        Project project = new Project(name, description, createdAt, updatedAt);
        project.id = id;
        if (variables != null) {
            project.variables.putAll(variables);
        }
        return project;
    }

    // Private constructor for reconstitution
    private Project(String name, String description, Instant createdAt, Instant updatedAt) {
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public List<SpecSource> getSpecSources() {
        return Collections.unmodifiableList(specSources);
    }

    public List<Endpoint> getEndpoints() {
        return Collections.unmodifiableList(endpoints);
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
     * Enforces the business rule: TestSuite names must be unique within a Project.
     *
     * @param testSuite the test suite to add
     * @throws IllegalArgumentException if suite is null, already exists, or name conflicts
     */
    public void addSuite(TestSuite testSuite) {
        Objects.requireNonNull(testSuite, "Test suite cannot be null");

        // Check for duplicates by ID if available
        if (testSuite.getId() != null && findTestSuiteById(testSuite.getId()).isPresent()) {
            throw new IllegalArgumentException("Suite with ID " + testSuite.getId() + " already exists in project");
        }

        // Enforce uniqueness constraint: no other suite in this project can have this name
        Optional<TestSuite> conflicting = testSuites.stream()
                .filter(s -> s.getName().equalsIgnoreCase(testSuite.getName()))
                .findFirst();

        if (conflicting.isPresent()) {
            throw new IllegalArgumentException(
                "Test suite with name '" + testSuite.getName() + "' already exists in project"
            );
        }

        this.testSuites.add(testSuite);
        touch(); // Project's collection changed
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

    /**
     * Get the most recent activity timestamp across the project and all its test suites.
     * This is useful for tracking "when was anything in this project last changed".
     *
     * Note: This only considers Project and TestSuite timestamps. If you need TestCase
     * timestamps as well, you'd need to iterate through all test cases in all suites.
     *
     * @return the most recent updatedAt timestamp
     */
    public Instant getLastActivityTime() {
        Instant max = this.updatedAt;

        for (TestSuite suite : testSuites) {
            if (suite.getUpdatedAt().isAfter(max)) {
                max = suite.getUpdatedAt();
            }
        }

        return max;
    }

    public void addSpecSource(SpecSource specSource){
        //Enforce uniqueness constraint: no other spec source in this project can have this name
        Optional<SpecSource> conflicting = specSources.stream()
                .filter(s -> s.getName().equalsIgnoreCase(specSource.getName()))
                .findFirst();

        if (conflicting.isPresent()) {
            throw new IllegalArgumentException(
                "Spec source with name '" + specSource.getName() + "' already exists in project"
            );
        }
        else{
            specSources.add(specSource);
            touch();
        }
    }

    public boolean removeSpecSource(Long specSourceId){
        //Remove spec source by id
        Optional<SpecSource> specSource = specSources.stream()
                .filter(s -> s.getId().equals(specSourceId))
                .findFirst();
        if (specSource.isPresent()) {
            specSources.remove(specSource.get());
            touch();
            return true;
        }
        return false;
    }

    public void updateSpecSource(Long specSourceId, SpecSource updated) {
        for (int i = 0; i < specSources.size(); i++) {
            if (specSources.get(i).getId().equals(specSourceId)) {
                updated.setId(specSourceId); // keep ID
                specSources.set(i, updated);
                touch();
                return;
            }
        }
    }

    public void addEnpoint (Endpoint endpoint){
        //Enforce uniqueness constraint: no other endpoint in this project can have this name
        Optional<Endpoint> conflicting = endpoints.stream()
                .filter(s -> s.getName().equalsIgnoreCase(endpoint.getName()))
                .findFirst();

        //enfore that the endpoint specsource exist in the project
        Optional<SpecSource> specSource = specSources.stream()
                .filter(s -> s.getId().equals(endpoint.getId()))
                .findFirst();

        if (specSource.isEmpty()) {
            throw new IllegalArgumentException(
                "Spec source with id '" + endpoint.getId() + "' does not exist in project"
            );
        }

        if (conflicting.isPresent()) {
            throw new IllegalArgumentException(
                "Endpoint with name '" + endpoint.getName() + "' already exists in project"
            );
        }
        else{
            endpoints.add(endpoint);
            touch();
        }

    }

    public boolean removeEndpoint(Endpoint endpoint){
        Optional<Endpoint> optionalEndpoint = endpoints.stream()
                .filter(e -> e.getId().equals(endpoint.getId()))
                .findFirst();
        if (optionalEndpoint.isPresent()) {
            endpoints.remove(endpoint);
            touch();
            return true;
        }
        return false;

    }

    // ========================================================================
    // BUSINESS INVARIANT ENFORCEMENT
    // Project enforces that TestSuite names are unique within the project.
    // These methods delegate to TestSuite but enforce the uniqueness constraint.
    // Note: These methods do NOT update Project.updatedAt since the Project
    // itself isn't changing - only the TestSuite is changing.
    // ========================================================================

    /**
     * Rename a test suite within this project.
     * Enforces the business rule: TestSuite names must be unique within a Project.
     *
     * @param testSuiteId the ID of the test suite to rename
     * @param newName the new name for the test suite
     * @throws IllegalArgumentException if suite not found, name invalid, or name conflicts
     */
    public void renameTestSuite(Long testSuiteId, String newName) {
        TestSuite suite = findTestSuiteById(testSuiteId)
            .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + testSuiteId));

        // Check if name is actually changing
        if (suite.getName().equalsIgnoreCase(newName.trim())) {
            return; // No change needed
        }

        // Enforce uniqueness constraint: no other suite in this project can have this name
        Optional<TestSuite> conflicting = testSuites.stream()
                .filter(s -> !s.getId().equals(testSuiteId)) // Exclude current suite
                .filter(s -> s.getName().equalsIgnoreCase(newName.trim()))
                .findFirst();

        if (conflicting.isPresent()) {
            throw new IllegalArgumentException(
                "Test suite with name '" + newName + "' already exists in project"
            );
        }

        suite.rename(newName);
        // Note: Project.updatedAt is NOT updated - only TestSuite changed
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
