package com.example.demo.core.api.mapper;

import com.example.demo.core.api.dto.response.execution.AssertionResultResponse;
import com.example.demo.core.api.dto.response.execution.TestCaseRunResponse;
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
        if (domain.getStartTime() != null && domain.getEndTime() != null) {
            durationMs = domain.getEndTime().toEpochMilli() - domain.getStartTime().toEpochMilli();
        }

        String actualResponse = null;
        String expectedResult = null;
        String errorMessage = domain.getErrorMessage();
        List<AssertionResultResponse> assertionResults = null;

        // Extract details from ApiTestRun (REST/SOAP tests)
        if (domain instanceof ApiTestRun apiRun) {
            if (apiRun.getActualResponse() != null) {
                actualResponse = apiRun.getActualResponse().getBody();
            }
            // TODO: Map assertion results when domain model is complete
        }

        return new TestCaseRunResponse(
                domain.getId(),
                domain.getTestCase() != null ? domain.getTestCase().getId() : null,
                domain.getTestCase() != null ? domain.getTestCase().getName() : "Unknown",
                domain.getStatus(),
                domain.getStartTime(),
                domain.getEndTime(),
                durationMs,
                actualResponse,
                expectedResult,
                errorMessage,
                assertionResults
        );
    }
}
