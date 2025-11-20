package com.example.demo.llm_adapter.dto.e2e;

/**
 * AI output: Configuration for extracting data from a step's response.
 * Maps to domain ExtractorItem record.
 *
 * Extractors capture values from step responses and store them as variables
 * for use in subsequent steps. Variables can be referenced in URLs, headers,
 * bodies, and assertions using ${variableName} syntax.
 */
public record ExtractorData(
    /**
     * Variable name to store the extracted value (max 50 chars).
     * Use descriptive names: orderId, authToken, customerId, etc.
     * Will be available as ${variableName} in subsequent steps.
     */
    String variableName,

    /**
     * Extraction method to use.
     * Valid values:
     * - "JSON_PATH": Extract from JSON response using JSONPath (e.g., "$.data.orderId")
     * - "XPATH": Extract from XML/SOAP response using XPath (e.g., "//order/@id")
     * - "HEADER": Extract from response header (e.g., "Location", "X-Request-Id")
     * - "REGEX": Extract using regex pattern (first capture group)
     */
    String extractorType,

    /**
     * Extraction path/pattern based on extractorType.
     * Examples:
     * - JSON_PATH: "$.data.orderId" or "$.user.token"
     * - XPATH: "//ns:order/@id" or "//response/userId/text()"
     * - HEADER: "Location" or "X-Correlation-Id"
     * - REGEX: "token=([a-zA-Z0-9]+)" or "id:\\s*\"([^\"]+)\""
     */
    String path,

    /**
     * Human-readable description of what is being extracted (max 200 chars).
     * Example: "Order ID for use in payment step"
     * Optional - can be null.
     */
    String description
) {
    public ExtractorData {
        if (variableName == null || variableName.isBlank()) {
            throw new IllegalArgumentException("Variable name required");
        }
        if (extractorType == null || extractorType.isBlank()) {
            throw new IllegalArgumentException("Extractor type required");
        }
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Extraction path required");
        }
    }
}
