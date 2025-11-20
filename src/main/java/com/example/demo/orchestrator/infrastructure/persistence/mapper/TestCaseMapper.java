package com.example.demo.orchestrator.infrastructure.persistence.mapper;

import com.example.demo.orchestrator.domain.test.TestCase;
import com.example.demo.orchestrator.domain.test.api.RestApiTest;
import com.example.demo.orchestrator.domain.test.api.SoapApiTest;
import com.example.demo.orchestrator.domain.test.e2e.E2eTest;
import com.example.demo.orchestrator.infrastructure.persistence.entity.test.*;
import org.springframework.stereotype.Component;

/**
 * Polymorphic mapper for TestCase hierarchy.
 *
 * Delegates to specific mappers based on discriminator type (REST/SOAP/E2E).
 */
@Component
public class TestCaseMapper {

    private final RestApiTestMapper restApiTestMapper;
    private final SoapApiTestMapper soapApiTestMapper;
    private final E2eTestMapper e2eTestMapper;

    public TestCaseMapper(
            RestApiTestMapper restApiTestMapper,
            SoapApiTestMapper soapApiTestMapper,
            E2eTestMapper e2eTestMapper) {
        this.restApiTestMapper = restApiTestMapper;
        this.soapApiTestMapper = soapApiTestMapper;
        this.e2eTestMapper = e2eTestMapper;
    }

    /**
     * Convert entity to domain (polymorphic).
     */
    public TestCase toDomain(TestCaseEntity entity) {
        if (entity == null) {
            return null;
        }

        return switch (entity) {
            case RestApiTestEntity restEntity -> restApiTestMapper.toDomain(restEntity);
            case SoapApiTestEntity soapEntity -> soapApiTestMapper.toDomain(soapEntity);
            case E2eTestEntity e2eEntity -> e2eTestMapper.toDomain(e2eEntity);
            default -> throw new IllegalArgumentException("Unknown test case type: " + entity.getClass());
        };
    }

    /**
     * Convert domain to entity (polymorphic).
     */
    public TestCaseEntity toEntity(TestCase domain) {
        if (domain == null) {
            return null;
        }

        return switch (domain) {
            case RestApiTest restTest -> restApiTestMapper.toEntity(restTest);
            case SoapApiTest soapTest -> soapApiTestMapper.toEntity(soapTest);
            case E2eTest e2eTest -> e2eTestMapper.toEntity(e2eTest);
            default -> throw new IllegalArgumentException("Unknown test case type: " + domain.getClass());
        };
    }
}
