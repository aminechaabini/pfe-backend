package com.example.demo.orchestrator.app.mapper.run;

import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.domain.run.TestSuiteRun;
import com.example.demo.orchestrator.persistence.entity.run.TestSuiteRunEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for TestSuiteRun domain <-> TestSuiteRunEntity persistence.
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {TestCaseRunMapper.class}
)
public interface TestSuiteRunMapper {

    TestSuiteRunEntity toEntity(TestSuiteRun domain);
    TestSuiteRun toDomain(TestSuiteRunEntity entity);
    List<TestSuiteRun> toDomainList(List<TestSuiteRunEntity> entityList);
    List<TestSuiteRunEntity> toEntityList(List<TestSuiteRun> domainList);
}
