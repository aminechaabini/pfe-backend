package com.example.demo.core.presentation.rest.mapper;

import com.example.demo.core.presentation.rest.dto.response.execution.AssertionResultResponse;
import com.example.demo.core.presentation.rest.dto.response.execution.TestCaseRunResponse;
import com.example.demo.core.domain.run.ApiTestRun;
import com.example.demo.core.domain.run.TestCaseRun;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper: TestCaseRun (domain) → TestCaseRunResponse (API DTO).
 */
@Mapper(componentModel = "spring")
public interface TestCaseRunResponseMapper {

    /**
     * Map test case run to response.
     * Includes full details for failure analysis.
     */
    default TestCaseRunResponse toResponse(TestCaseRun domain) {
        if (domain == null) {
            return null;
        }

        Long durationMs = null;
        if (domain.getStartedAt() != null && domain.getCompletedAt() != null) {
            durationMs = domain.getCompletedAt().toEpochMilli() - domain.getStartedAt().toEpochMilli();
        }

        String actualResponse = null;
        String expectedResult = null;
        String errorMessage = null;
        List<AssertionResultResponse> assertionResults = null;

        // Extract details from ApiTestRun (REST/SOAP tests)
        if (domain instanceof ApiTestRun apiRun) {
            // TODO: Map assertion results when domain model is complete
        }

        return new TestCaseRunResponse(
                domain.getId(),
                domain.getTestCase() != null ? domain.getTestCase().getId() : null,
                domain.getTestCase() != null ? domain.getTestCase().getName() : "Unknown",
                domain.getStatus(),
                domain.getStartedAt(),
                domain.getCompletedAt(),
                durationMs,
                actualResponse,
                expectedResult,
                errorMessage,
                assertionResults
        );
    }
}
