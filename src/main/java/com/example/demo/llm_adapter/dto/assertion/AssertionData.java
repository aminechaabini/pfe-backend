package com.example.demo.llm_adapter.dto.assertion;

/**
 * AI output: Simple data representation of an Assertion.
 * Maps to domain Assertion record.
 *
 * Type values for REST tests:
 * - "STATUS_EQUALS" - Check HTTP status code
 * - "JSONPATH_EQUALS" - Check JSON field equals value
 * - "JSONPATH_EXISTS" - Check JSON field exists
 * - "JSON_SCHEMA_VALID" - Validate against JSON schema
 * - "HEADER_EQUALS" - Check HTTP header value
 * - "BODY_CONTAINS" - Check response body contains text
 * - "RESPONSE_TIME_LESS_THAN" - Check response time
 * - "REGEX_MATCH" - Check against regex pattern
 *
 * Type values for SOAP tests (XML only):
 * - "STATUS_EQUALS"
 * - "XPATH_EQUALS" - Check XML element equals value
 * - "XPATH_EXISTS" - Check XML element exists
 * - "XSD_VALID" - Validate against XSD schema
 * - "HEADER_EQUALS"
 * - "BODY_CONTAINS"
 * - "RESPONSE_TIME_LESS_THAN"
 * - "REGEX_MATCH"
 */
public record AssertionData(
    /**
     * Assertion type (see class javadoc for valid values).
     * Example: "STATUS_EQUALS", "JSONPATH_EQUALS", "XPATH_EQUALS"
     */
    String type,

    /**
     * Target to validate.
     * Examples:
     * - For STATUS_EQUALS: not used (can be empty string)
     * - For JSONPATH_EQUALS: "$.data.orderId"
     * - For XPATH_EQUALS: "//order/@id"
     * - For HEADER_EQUALS: "Content-Type"
     */
    String target,

    /**
     * Expected value or condition.
     * Examples:
     * - For STATUS_EQUALS: "200", "201", "400"
     * - For JSONPATH_EQUALS: "pending", "123"
     * - For XPATH_EQUALS: "12345"
     * - For HEADER_EQUALS: "application/json"
     * - For RESPONSE_TIME_LESS_THAN: "500" (milliseconds)
     */
    String expected
) {
    public AssertionData {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Assertion type required");
        }
        if (target == null) {
            throw new IllegalArgumentException("Assertion target required (use empty string if not applicable)");
        }
        if (expected == null) {
            throw new IllegalArgumentException("Expected value required");
        }
    }
}
