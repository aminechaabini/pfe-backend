package com.example.demo.shared.context;

import com.example.demo.ai.dto.CreateSoapApiTestRequest;
import java.util.List;

/**
 * Context for SpecUpdateAnalyzer AI service (SOAP).
 * Contains old and new WSDL specs plus existing tests to analyze impact.
 */
public record SoapSpecUpdateAnalysisContext(
    String oldSpecContent,              // Previous WSDL spec
    String newSpecContent,              // New WSDL spec
    List<CreateSoapApiTestRequest> existingTests  // Current tests to analyze
) implements Context {
}
