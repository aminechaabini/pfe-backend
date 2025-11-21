package com.example.demo.core.domain.test.e2e;

import com.example.demo.core.domain.test.assertion.Assertion;
import com.example.demo.core.domain.test.request.HttpRequest;
import com.example.demo.core.domain.test.request.body.Body;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A single step in an E2E test with request, assertions, and data extraction.
 * Now a proper domain entity with identity and audit fields.
 */
public class E2eStep {

    // Constants for validation
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    // Identity
    private Long id;

    // Business fields
    private String name;
    private String description;
    private Integer orderIndex;
    private HttpRequest<Body> httpRequest;
    private final List<Assertion> assertions = new ArrayList<>();
    private final List<ExtractorItem> extractorItems = new ArrayList<>();

    // Audit fields
    private final Instant createdAt;
    private Instant updatedAt;

    /**
     * Create an E2E step with required fields.
     *
     * @param name the step name (e.g., "Login", "Create Order")
     * @param description optional description of what this step does
     * @param orderIndex the order of this step in the E2E test
     */
    public E2eStep(String name, String description, Integer orderIndex) {
        validateName(name);
        this.name = name.trim();
        validateDescription(description);
        this.description = description == null ? "" : description.trim();
        this.orderIndex = orderIndex;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Default constructor for frameworks that require it.
     */
    public E2eStep() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    // Identity getters/setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Cannot change ID once set");
        }
        this.id = Objects.requireNonNull(id, "ID cannot be null");
    }

    // Business field getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public HttpRequest<Body> getHttpRequest() {
        return httpRequest;
    }

    public List<Assertion> getAssertions() {
        return Collections.unmodifiableList(assertions);
    }

    public List<ExtractorItem> getExtractorItems() {
        return Collections.unmodifiableList(extractorItems);
    }

    // Audit field getters
    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Rename the step.
     *
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
     * Update the step description.
     *
     * @param newDescription the new description (will be trimmed)
     * @throws IllegalArgumentException if description is invalid
     */
    public void updateDescription(String newDescription) {
        validateDescription(newDescription);
        String trimmed = newDescription == null ? "" : newDescription.trim();
        if (trimmed.equals(this.description)) return;
        this.description = trimmed;
        touch();
    }

    /**
     * Set the order index for this step.
     *
     * @param orderIndex the new order index
     */
    public void setOrderIndex(Integer orderIndex) {
        if (Objects.equals(this.orderIndex, orderIndex)) return;
        this.orderIndex = orderIndex;
        touch();
    }

    /**
     * Set the HTTP request for this step.
     *
     * @param httpRequest the HTTP request
     * @throws NullPointerException if httpRequest is null
     */
    public void setHttpRequest(HttpRequest<Body> httpRequest) {
        this.httpRequest = Objects.requireNonNull(httpRequest, "HTTP request cannot be null");
        touch();
    }

    // Assertion operations
    public void addAssertion(Assertion assertion) {
        Objects.requireNonNull(assertion, "Assertion cannot be null");
        this.assertions.add(assertion);
        touch();
    }

    public boolean removeAssertion(Assertion assertion) {
        boolean removed = this.assertions.remove(assertion);
        if (removed) touch();
        return removed;
    }

    public void clearAssertions() {
        if (!this.assertions.isEmpty()) {
            this.assertions.clear();
            touch();
        }
    }

    // Extractor operations
    public void addExtractor(ExtractorItem extractor) {
        Objects.requireNonNull(extractor, "Extractor item cannot be null");
        this.extractorItems.add(extractor);
        touch();
    }

    public boolean removeExtractor(ExtractorItem extractor) {
        boolean removed = this.extractorItems.remove(extractor);
        if (removed) touch();
        return removed;
    }

    public void clearExtractors() {
        if (!this.extractorItems.isEmpty()) {
            this.extractorItems.clear();
            touch();
        }
    }

    /**
     * Get the number of assertions.
     */
    public int getAssertionCount() {
        return assertions.size();
    }

    /**
     * Get the number of extractors.
     */
    public int getExtractorCount() {
        return extractorItems.size();
    }

    /**
     * Validate the step name.
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("E2E step name cannot be null or blank");
        }
        if (name.trim().length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                String.format("E2E step name must be at most %d characters", MAX_NAME_LENGTH)
            );
        }
    }

    /**
     * Validate the step description.
     */
    private void validateDescription(String description) {
        if (description == null) return; // null is acceptable
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                String.format("E2E step description must be at most %d characters", MAX_DESCRIPTION_LENGTH)
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
