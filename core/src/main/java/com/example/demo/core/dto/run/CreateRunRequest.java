package com.example.demo.core.dto.run;

import jakarta.validation.constraints.AssertTrue;

/**
 * Exactly one of suiteId or testId must be provided.
 */
public record CreateRunRequest(
    Long suiteId,
    Long testId
) {
    @AssertTrue(message = "Exactly one of suiteId or testId must be provided")
    public boolean isExactlyOneTargetSet() {
        return (suiteId != null) ^ (testId != null);
    }
}
