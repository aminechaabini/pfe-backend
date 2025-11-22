package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.run.ApiTestRun;
import com.example.demo.core.domain.run.E2eTestRun;
import com.example.demo.core.domain.run.TestCaseRun;
import com.example.demo.core.infrastructure.persistence.entity.run.ApiTestRunEntity;
import com.example.demo.core.infrastructure.persistence.entity.run.E2eTestRunEntity;
import com.example.demo.core.infrastructure.persistence.entity.run.TestCaseRunEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for TestCaseRun ↔ TestCaseRunEntity.
 *
 * Handles polymorphic mapping for ApiTestRun and E2eTestRun.
 */
@Mapper(componentModel = "spring")
public interface TestCaseRunMapper {

    /**
     * Polymorphic toDomain - dispatches to concrete methods.
     */
    default TestCaseRun toDomain(TestCaseRunEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof ApiTestRunEntity) {
            return toDomain((ApiTestRunEntity) entity);
        } else if (entity instanceof E2eTestRunEntity) {
            return toDomain((E2eTestRunEntity) entity);
        }
        throw new IllegalArgumentException("Unknown entity type: " + entity.getClass());
    }

    @Mapping(target = "testCase", ignore = true)  // Not loaded in run entities
    @Mapping(target = "assertionResults", ignore = true)  // Loaded separately
    ApiTestRun toDomain(ApiTestRunEntity entity);

    @Mapping(target = "testCase", ignore = true)  // Not loaded in run entities
    @Mapping(target = "stepRuns", ignore = true)  // Loaded separately
    E2eTestRun toDomain(E2eTestRunEntity entity);

    /**
     * Polymorphic toEntity - dispatches to concrete methods.
     */
    default TestCaseRunEntity toEntity(TestCaseRun domain) {
        if (domain == null) {
            return null;
        }
        if (domain instanceof ApiTestRun) {
            return toEntity((ApiTestRun) domain);
        } else if (domain instanceof E2eTestRun) {
            return toEntity((E2eTestRun) domain);
        }
        throw new IllegalArgumentException("Unknown domain type: " + domain.getClass());
    }

    @Mapping(target = "testSuiteRunId", ignore = true)  // Set by repository
    @Mapping(target = "testCaseId", ignore = true)  // Set by repository
    @Mapping(target = "testCaseName", ignore = true)  // Set by repository
    @Mapping(target = "errorMessage", ignore = true)  // Set during execution
    @Mapping(target = "actualStatusCode", ignore = true)  // Set during execution
    @Mapping(target = "actualResponseBody", ignore = true)  // Set during execution
    @Mapping(target = "actualResponseHeadersJson", ignore = true)  // Set during execution
    @Mapping(target = "responseTimeMs", ignore = true)  // Set during execution
    @Mapping(target = "assertionResults", ignore = true)  // Set during execution
    ApiTestRunEntity toEntity(ApiTestRun domain);

    @Mapping(target = "testSuiteRunId", ignore = true)  // Set by repository
    @Mapping(target = "testCaseId", ignore = true)  // Set by repository
    @Mapping(target = "testCaseName", ignore = true)  // Set by repository
    @Mapping(target = "errorMessage", ignore = true)  // Set during execution
    @Mapping(target = "stepRuns", ignore = true)  // Set during execution
    E2eTestRunEntity toEntity(E2eTestRun domain);

    /**
     * Update entity from domain.
     */
    default void updateEntityFromDomain(@MappingTarget TestCaseRunEntity entity, TestCaseRun domain) {
        if (entity == null || domain == null) {
            return;
        }
        if (entity instanceof ApiTestRunEntity && domain instanceof ApiTestRun) {
            updateEntityFromDomain((ApiTestRunEntity) entity, (ApiTestRun) domain);
        } else if (entity instanceof E2eTestRunEntity && domain instanceof E2eTestRun) {
            updateEntityFromDomain((E2eTestRunEntity) entity, (E2eTestRun) domain);
        } else {
            throw new IllegalArgumentException("Entity and domain types don't match");
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "testSuiteRunId", ignore = true)
    @Mapping(target = "testCaseId", ignore = true)
    @Mapping(target = "testCaseName", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "actualStatusCode", ignore = true)
    @Mapping(target = "actualResponseBody", ignore = true)
    @Mapping(target = "actualResponseHeadersJson", ignore = true)
    @Mapping(target = "responseTimeMs", ignore = true)
    @Mapping(target = "assertionResults", ignore = true)
    void updateEntityFromDomain(@MappingTarget ApiTestRunEntity entity, ApiTestRun domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "testSuiteRunId", ignore = true)
    @Mapping(target = "testCaseId", ignore = true)
    @Mapping(target = "testCaseName", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "stepRuns", ignore = true)
    void updateEntityFromDomain(@MappingTarget E2eTestRunEntity entity, E2eTestRun domain);
}
