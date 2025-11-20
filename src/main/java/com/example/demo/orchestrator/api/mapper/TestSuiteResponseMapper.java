package com.example.demo.orchestrator.api.mapper;

import com.example.demo.orchestrator.api.dto.response.suite.TestSuiteDetailResponse;
import com.example.demo.orchestrator.api.dto.response.suite.TestSuiteResponse;
import com.example.demo.orchestrator.domain.test.test_suite.TestSuite;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper: TestSuite (domain) → TestSuiteResponse (API DTO).
 */
@Mapper(componentModel = "spring", uses = {TestCaseResponseMapper.class, EndpointResponseMapper.class})
public interface TestSuiteResponseMapper {

    /**
     * Map to summary response.
     */
    default TestSuiteResponse toResponse(TestSuite domain) {
        if (domain == null) {
            return null;
        }

        Long endpointId = domain.getEndpoint() != null ? domain.getEndpoint().getId() : null;

        return new TestSuiteResponse(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getVariables(),
                domain.getTestCases().size(),
                endpointId,
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    /**
     * Map to detail response (includes test cases and endpoint).
     */
    default TestSuiteDetailResponse toDetailResponse(TestSuite domain) {
        if (domain == null) {
            return null;
        }

        TestCaseResponseMapper testCaseMapper = new TestCaseResponseMapperImpl();
        EndpointResponseMapper endpointMapper = new EndpointResponseMapperImpl();

        Long endpointId = domain.getEndpoint() != null ? domain.getEndpoint().getId() : null;

        return new TestSuiteDetailResponse(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getVariables(),
                domain.getTestCases().size(),
                endpointId,
                endpointMapper.toResponse(domain.getEndpoint()),
                testCaseMapper.toResponseList(domain.getTestCases()),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    List<TestSuiteResponse> toResponseList(List<TestSuite> domains);
}
