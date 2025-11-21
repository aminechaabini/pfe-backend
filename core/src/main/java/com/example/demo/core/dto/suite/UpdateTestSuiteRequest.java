package com.example.demo.core.dto.suite;

import jakarta.validation.constraints.Size;

/** ID is in the path. */
public record UpdateTestSuiteRequest(
    @Size(max = 255) String name,
    @Size(max = 1000) String description,
    Long version
) {}
