package com.example.demo.core.domain.spec;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

public abstract class Endpoint {
    // Common to all endpoint types
    protected Long id;
    protected String name;
    protected String summary;
    protected String operationId; // used differently by each type
    protected String specDetails; // JSON
    protected Long specSourceId;  // Reference to owning SpecSource
    protected Long projectId;     // Reference to owning Project
    protected Instant createdAt;
    protected Instant updatedAt;
    protected Set<Long> testSuiteIds;

    public String getName() {
        return name;
    }

    // Abstract methods (each subclass implements)
    public abstract String getDisplayName();

    public abstract String getUniqueKey();

    public abstract EndpointType getType();

    public abstract boolean hasPathParameters();

    public Long getId() {
        return id;
    }

    public String getSummary() {
        return summary;
    }

    public String getOperationId() {
        return operationId;
    }

    public String getSpecDetails() {
        return specDetails;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getSpecSourceId() {
        return specSourceId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Set<Long> getTestSuiteIds() {
        return testSuiteIds != null ? Collections.unmodifiableSet(testSuiteIds) : Collections.emptySet();
    }
}
