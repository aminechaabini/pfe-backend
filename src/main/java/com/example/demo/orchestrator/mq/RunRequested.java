package com.example.demo.orchestrator.mq;

import java.time.Instant;
import java.util.Map;

public record RunRequested(
        String runId,
        String projectId,
        String testId,               // if you only run one test
        String idempotencyKey,       // optional
        Map<String, String> overrides,   // e.g., baseUrl, timeoutMs
        Instant requestedAt          // for debugging/ordering
) {}
