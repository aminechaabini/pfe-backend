package com.example.demo.orchestrator.app.mapper.definition;

import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.domain.test.TestCase;
import com.example.demo.orchestrator.domain.test.api.RestApiTest;
import com.example.demo.orchestrator.domain.test.api.SoapApiTest;
import com.example.demo.orchestrator.domain.test.e2e.E2eTest;
import com.example.demo.orchestrator.persistence.entity.test.E2eTestEntity;
import com.example.demo.orchestrator.persistence.entity.test.RestApiTestEntity;
import com.example.demo.orchestrator.persistence.entity.test.SoapApiTestEntity;
import com.example.demo.orchestrator.persistence.entity.test.TestCaseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

import java.util.List;

/**
 * Abstract polymorphic mapper for TestCase domain <-> TestCaseEntity persistence.
 *
 * Handles the polymorphic hierarchy using MapStruct's @SubclassMapping:
 *
 * Domain:                     Entity:
 * TestCase (abstract)         TestCaseEntity (abstract, SINGLE_TABLE)
 * ├── RestApiTest        ↔    ├── RestApiTestEntity
 * ├── SoapApiTest        ↔    ├── SoapApiTestEntity
 * └── E2eTest            ↔    └── E2eTestEntity
 *
 * MapStruct automatically delegates to the specific mapper implementations
 * based on the runtime type of the object.
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {
        RestApiTestMapper.class,
        SoapApiTestMapper.class,
        E2eTestMapper.class
    }
)
public interface TestCaseMapper {

    /**
     * Convert domain TestCase to persistence TestCaseEntity.
     * Automatically delegates to the correct subclass mapper.
     */
    @SubclassMapping(source = RestApiTest.class, target = RestApiTestEntity.class)
    @SubclassMapping(source = SoapApiTest.class, target = SoapApiTestEntity.class)
    @SubclassMapping(source = E2eTest.class, target = E2eTestEntity.class)
    TestCaseEntity toEntity(TestCase domain);

    /**
     * Convert persistence TestCaseEntity to domain TestCase.
     * Automatically delegates to the correct subclass mapper.
     */
    @SubclassMapping(source = RestApiTestEntity.class, target = RestApiTest.class)
    @SubclassMapping(source = SoapApiTestEntity.class, target = SoapApiTest.class)
    @SubclassMapping(source = E2eTestEntity.class, target = E2eTest.class)
    TestCase toDomain(TestCaseEntity entity);

    /**
     * Convert list of domain TestCases to list of TestCaseEntities.
     * Handles polymorphism automatically.
     */
    List<TestCaseEntity> toEntityList(List<TestCase> domainList);

    /**
     * Convert list of TestCaseEntities to list of domain TestCases.
     * Handles polymorphism automatically.
     */
    List<TestCase> toDomainList(List<TestCaseEntity> entityList);
}
