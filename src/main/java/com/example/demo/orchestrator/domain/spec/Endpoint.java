package com.example.demo.orchestrator.domain.spec;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

public abstract class Endpoint {
    // Common to all endpoint types
    protected Long id;
    protected String Name;
    protected String summary;
    protected String operationId; // used differently by each type
    protected String specDetails; // JSON
    protected Instant createdAt;
    protected Set<Long> testSuiteIds;

    public String getName() {
        return Name;
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

    public Set<Long> getTestSuiteIds() {
        return Collections.unmodifiableSet(testSuiteIds);
    }
}
