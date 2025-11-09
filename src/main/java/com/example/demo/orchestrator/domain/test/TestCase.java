package com.example.demo.orchestrator.domain.test;

import java.time.Instant;

public abstract class TestCase {
    
    // Constants for validation
    protected static final int MAX_NAME_LENGTH = 40;
    protected static final int MAX_DESCRIPTION_LENGTH = 2000;
    
    private Long id;
    private String name;
    private String description;
    protected final Instant createdAt;
    protected Instant updatedAt;

    protected TestCase(String name, String description) {
        validateName(name);
        this.name = name.trim();
        validateDescription(description);
        this.description = description == null ? "" : description.trim();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

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
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Rename the test case.
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
     * Update the description.
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
     * Validate the test case name.
     */
    protected void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Test case name cannot be null or blank");
        }
        if (name.trim().length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Test case name must be at most %d characters", MAX_NAME_LENGTH)
            );
        }
    }

    /**
     * Validate the test case description.
     */
    protected void validateDescription(String description) {
        if (description == null) return; // null is acceptable, will be converted to empty string
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Test case description must be at most %d characters", MAX_DESCRIPTION_LENGTH)
            );
        }
    }
    
    /**
     * Update the updatedAt timestamp.
     */
    protected void touch() {
        this.updatedAt = Instant.now();
    }
}
