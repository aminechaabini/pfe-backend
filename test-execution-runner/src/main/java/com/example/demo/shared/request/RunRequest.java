package com.example.demo.shared.request;

/**
 * Base interface for all run requests.
 */
public sealed interface RunRequest permits ApiRunRequest, E2eRunRequest {
    String runId();
}
