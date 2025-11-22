package com.example.demo.core.presentation.rest.mapper;

import com.example.demo.core.presentation.rest.dto.response.suite.TestCaseResponse;
import com.example.demo.core.domain.test.TestCase;
import com.example.demo.core.domain.test.api.RestApiTest;
import com.example.demo.core.domain.test.api.SoapApiTest;
import com.example.demo.core.domain.test.e2e.E2eTest;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper: TestCase (domain) → TestCaseResponse (API DTO).
 * Handles polymorphic mapping.
 */
@Mapper(componentModel = "spring")
public interface TestCaseResponseMapper {

    /**
     * Map test case to response (polymorphic).
     */
    default TestCaseResponse toResponse(TestCase domain) {
        if (domain == null) {
            return null;
        }

        String type = switch (domain) {
            case RestApiTest ignored -> "REST_API";
            case SoapApiTest ignored -> "SOAP_API";
            case E2eTest ignored -> "E2E";
            default -> "UNKNOWN";
        };

        return new TestCaseResponse(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                type,
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    default List<TestCaseResponse> toResponseList(List<TestCase> domains) {
        if (domains == null) {
            return null;
        }
        return domains.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
