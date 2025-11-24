package com.example.demo.common.context.dto.analysis;

/**
 * AI output: Description of a test affected by spec changes.
 */
public record AffectedTest(
    /**
     * Name of the affected test.
     * Example: "Create User - Valid Data"
     */
    String testName,

    /**
     * Endpoint/operation this test targets.
     * Example: "POST /api/users" or "CreateUser"
     */
    String endpoint,

    /**
     * Description of how the spec change affects this test (max 500 chars).
     * Example: "Field 'email' is now required. Test sends request without email which will now fail validation."
     */
    String impactDescription,

    /**
     * Suggested action to take for this test.
     * Valid values:
     * - "regenerate": Test should be regenerated using spec2suite with new spec
     * - "delete": Test targets removed endpoint and should be deleted
     * - "manual_review": Complex change requiring manual developer review
     */
    String suggestedAction
) {
    public AffectedTest {
        if (testName == null || testName.isBlank()) {
            throw new IllegalArgumentException("Test name required");
        }
        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalArgumentException("Endpoint required");
        }
        if (impactDescription == null || impactDescription.isBlank()) {
            throw new IllegalArgumentException("Impact description required");
        }
        if (suggestedAction == null || suggestedAction.isBlank()) {
            throw new IllegalArgumentException("Suggested action required");
        }
    }
}
