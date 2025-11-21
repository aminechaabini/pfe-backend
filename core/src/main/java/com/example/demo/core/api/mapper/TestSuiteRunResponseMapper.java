package com.example.demo.core.api.mapper;

import com.example.demo.core.api.dto.response.execution.TestCaseRunSummary;
import com.example.demo.core.api.dto.response.execution.TestSuiteRunResponse;
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
        if (domain.getStartTime() != null && domain.getEndTime() != null) {
            durationMs = domain.getEndTime().toEpochMilli() - domain.getStartTime().toEpochMilli();
        }

        List<TestCaseRunSummary> summaries = domain.getTestCaseRuns().stream()
                .map(tcr -> {
                    Long tcDuration = null;
                    if (tcr.getStartTime() != null && tcr.getEndTime() != null) {
                        tcDuration = tcr.getEndTime().toEpochMilli() - tcr.getStartTime().toEpochMilli();
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
                domain.getStartTime(),
                domain.getEndTime(),
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
