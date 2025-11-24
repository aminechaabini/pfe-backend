package com.example.demo.shared.request;

import java.util.List;
import java.util.Map;

/**
 * Request to execute an E2E test workflow.
 */
public record E2eRunRequest(
    String runId,
    List<E2eStepRequest> steps,
    Map<String, String> variables
) implements RunRequest {
}
