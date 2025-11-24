package com.example.demo.shared.result;

/**
 * Base interface for all run results.
 */
public sealed interface RunResult permits ApiRunResult, E2eRunResult {
    String runId();
    String status();
    long duration();
}
