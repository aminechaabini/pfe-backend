package com.example.demo.shared.request;

import com.example.demo.shared.valueobject.AssertionSpec;
import com.example.demo.shared.valueobject.ExtractorSpec;
import com.example.demo.shared.valueobject.HttpRequestData;

import java.util.List;

/**
 * Single step in an E2E test workflow.
 */
public record E2eStepRequest(
    String stepId,
    String stepName,
    int stepOrder,
    String protocol,        // REST or SOAP
    HttpRequestData httpRequest,
    List<AssertionSpec> assertions,
    List<ExtractorSpec> extractors
) {
}
