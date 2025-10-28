package com.example.demo.orchestrator.dto.project;

import jakarta.validation.constraints.Size;

/** ID is in the path; include version if you use optimistic locking at the API edge. */
public record UpdateProjectRequest(
    @Size(max = 255) String name,
    @Size(max = 1000) String description,
    Long version
) {
    public String getName() { return name;}
    public String getDescription() { return description;}
}