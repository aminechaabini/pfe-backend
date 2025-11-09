package com.example.demo.orchestrator.domain.test.test_suite;

import com.example.demo.orchestrator.domain.test.TestCase;

import java.time.Instant;
import java.util.*;

public class TestSuite {

    // Constants for validation
    private static final int MAX_NAME_LENGTH = 40;
    private static final int MAX_DESCRIPTION_LENGTH = 2000;
    private static final int MAX_VARIABLE_NAME_LENGTH = 200;
    private static final int MAX_VARIABLE_VALUE_LENGTH = 2000;

    private Long id;
    private String name;
    private String description;
    private final Map<String, String> variables = new HashMap<>();
    private final List<TestCase> testCases = new ArrayList<>();
    private final Instant createdAt;
    protected Instant updatedAt;

    public TestSuite(String name, String description) {
        validateName(name);
        this.name = name.trim();
        validateDescription(description);
        this.description = description == null ? "" : description.trim();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Cannot change ID once set");
        }
        this.id = id;
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

    public List<TestCase> getTestCases() {
        return Collections.unmodifiableList(testCases);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Rename the test suite.
     */
    public void rename(String newName) {
        validateName(newName);
        String trimmed = newName.trim();
        if (trimmed.equals(this.name)) return;
        this.name = trimmed;
        touch();
    }

    /**
     * Update the description.
     */
    public void updateDescription(String newDescription) {
        validateDescription(newDescription);
        String trimmed = newDescription == null ? "" : newDescription.trim();
        if (trimmed.equals(this.description)) return;
        this.description = trimmed;
        touch();
    }

    /**
     * Set or update a variable.
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
     * @return true if the variable existed and was removed
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
     * Add a test case to the suite.
     */
    public void addTestCase(TestCase testCase) {
        Objects.requireNonNull(testCase, "Test case cannot be null");

        // Check for duplicates by ID if available
        if (testCase.getId() != null && findTestCaseById(testCase.getId()).isPresent()) {
            throw new IllegalArgumentException("Test case with ID " + testCase.getId() + " already exists in suite");
        }

        this.testCases.add(testCase);
        touch();
    }

    /**
     * Remove a test case from the suite.
     * @return true if the test case was removed
     */
    public boolean removeTestCase(TestCase testCase) {
        Objects.requireNonNull(testCase, "Test case cannot be null");
        boolean result = this.testCases.remove(testCase);
        if (result) touch();
        return result;
    }

    /**
     * Find a test case by its ID.
     */
    public Optional<TestCase> findTestCaseById(Long id) {
        if (id == null) return Optional.empty();
        return testCases.stream()
                .filter(tc -> id.equals(tc.getId()))
                .findFirst();
    }

    // ========================================================================
    // AGGREGATE ROOT FACADE METHODS
    // TestSuite is the aggregate root for TestCases. Modifications to TestCases
    // should go through these methods to maintain consistency and update timestamps.
    // ========================================================================

    /**
     * Rename a test case within this test suite.
     * This maintains the aggregate boundary by keeping TestSuite in control.
     *
     * @param testCaseId the ID of the test case to rename
     * @param newName the new name for the test case
     * @throws IllegalArgumentException if test case not found or name invalid
     */
    public void renameTestCase(Long testCaseId, String newName) {
        TestCase testCase = findTestCaseById(testCaseId)
            .orElseThrow(() -> new IllegalArgumentException("Test case not found: " + testCaseId));
        testCase.rename(newName);
        touch(); // TestSuite aggregate is modified
    }

    /**
     * Update a test case's description within this test suite.
     *
     * @param testCaseId the ID of the test case
     * @param newDescription the new description
     * @throws IllegalArgumentException if test case not found or description invalid
     */
    public void updateTestCaseDescription(Long testCaseId, String newDescription) {
        TestCase testCase = findTestCaseById(testCaseId)
            .orElseThrow(() -> new IllegalArgumentException("Test case not found: " + testCaseId));
        testCase.updateDescription(newDescription);
        touch();
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Test suite name cannot be null or blank");
        }
        if (name.trim().length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Test suite name must be at most %d characters", MAX_NAME_LENGTH)
            );
        }
    }

    private void validateDescription(String description) {
        if (description == null) return;
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Test suite description must be at most %d characters", MAX_DESCRIPTION_LENGTH)
            );
        }
    }

    protected void touch() {
        this.updatedAt = Instant.now();
    }
}
