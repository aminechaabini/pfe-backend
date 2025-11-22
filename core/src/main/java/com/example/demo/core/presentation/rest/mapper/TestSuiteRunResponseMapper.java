package com.example.demo.core.presentation.rest.mapper;

import com.example.demo.core.presentation.rest.dto.response.execution.TestCaseRunSummary;
import com.example.demo.core.presentation.rest.dto.response.execution.TestSuiteRunResponse;
import com.example.demo.core.domain.run.TestSuiteRun;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper: TestSuiteRun (domain) → TestSuiteRunResponse (API DTO).
 */
@Mapper(componentModel = "spring")
public interface TestSuiteRunResponseMapper {

    /**
     * Map test suite run to response.
     */
    default TestSuiteRunResponse toResponse(TestSuiteRun domain) {
        if (domain == null) {
            return null;
        }

        Long durationMs = null;
        if (domain.getStartedAt() != null && domain.getCompletedAt() != null) {
            durationMs = domain.getCompletedAt().toEpochMilli() - domain.getStartedAt().toEpochMilli();
        }

        List<TestCaseRunSummary> summaries = domain.getTestCaseRuns().stream()
                .map(tcr -> {
                    Long tcDuration = null;
                    if (tcr.getStartedAt() != null && tcr.getCompletedAt() != null) {
                        tcDuration = tcr.getCompletedAt().toEpochMilli() - tcr.getStartedAt().toEpochMilli();
                    }
                    return new TestCaseRunSummary(
                            tcr.getId(),
                            tcr.getTestCase() != null ? tcr.getTestCase().getName() : "Unknown",
                            tcr.getStatus(),
                            tcDuration
                    );
                })
                .collect(Collectors.toList());

        return new TestSuiteRunResponse(
                domain.getId(),
                domain.getTestSuite() != null ? domain.getTestSuite().getId() : null,
                domain.getTestSuite() != null ? domain.getTestSuite().getName() : "Unknown",
                domain.getStatus(),
                domain.getStartedAt(),
                domain.getCompletedAt(),
                durationMs,
                (int) domain.getTestCaseRuns().size(),
                (int) domain.getPassedTestCasesCount(),
                (int) domain.getFailedTestCasesCount(),
                summaries
        );
    }

    default List<TestSuiteRunResponse> toResponseList(List<TestSuiteRun> domains) {
        if (domains == null) {
            return null;
        }
        return domains.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
