package com.example.demo.shared.events;

import dev.langchain4j.model.output.structured.Description;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Description("A run request")
public record RunRequest(
        String runId,
        HttpRequest httpRequest,
        Protocol protocol,
        List<AssertionSpec> assertions       // the single test to execute

) {
}
