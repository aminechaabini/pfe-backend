package com.example.demo.shared.result;

/**
 * Base interface for test execution results.
 * Sealed to ensure only API and E2E results are permitted.
 */
public sealed interface RunResult permits ApiRunResult, E2eRunResult {

    String runId();

    String status();        // "PASS", "FAIL", "ERROR"

    long durationMs();
}
