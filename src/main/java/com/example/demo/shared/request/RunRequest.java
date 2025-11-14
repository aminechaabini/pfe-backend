package com.example.demo.shared.request;

/**
 * Base interface for all run requests.
 * Allows polymorphic handling in RunnerService queue.
 */
public sealed interface RunRequest permits ApiRunRequest, E2eRunRequest {
    String runId();
}
