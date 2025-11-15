package com.example.demo.orchestrator.domain.spec;

import java.time.Instant;
import java.util.Set;

public abstract class Endpoint {

import java.time.Instant;
import java.util.Set;

    public abstract class Endpoint {
        // Common to all endpoint types
        public Long id;
        public String summary;
        public String operationId; // used differently by each type
        public String specDetails; // JSON
        public Instant createdAt;
        public Set<Long> testSuiteIds;

        // Abstract methods (each subclass implements)
        public abstract String getDisplayName();
        public abstract String getUniqueKey();
        public abstract EndpointType getType();
        public abstract boolean hasPathParameters();
    }