package com.example.demo.orchestrator.app.mapper.run;

import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.domain.run.ApiTestRun;
import com.example.demo.orchestrator.domain.run.E2eTestRun;
import com.example.demo.orchestrator.domain.run.TestCaseRun;
import com.example.demo.orchestrator.persistence.entity.run.ApiTestRunEntity;
import com.example.demo.orchestrator.persistence.entity.run.E2eTestRunEntity;
import com.example.demo.orchestrator.persistence.entity.run.TestCaseRunEntity;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

import java.util.List;

/**
 * Abstract polymorphic mapper for TestCaseRun domain <-> TestCaseRunEntity persistence.
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {ApiTestRunMapper.class, E2eTestRunMapper.class}
)
public interface TestCaseRunMapper {

    @SubclassMapping(source = ApiTestRunEntity.class, target = ApiTestRun.class)
    @SubclassMapping(source = E2eTestRunEntity.class, target = E2eTestRun.class)
    TestCaseRun toDomain(TestCaseRunEntity entity);

    @SubclassMapping(source = ApiTestRun.class, target = ApiTestRunEntity.class)
    @SubclassMapping(source = E2eTestRun.class, target = E2eTestRunEntity.class)
    TestCaseRunEntity toEntity(TestCaseRun domain);

    List<TestCaseRun> toDomainList(List<TestCaseRunEntity> entityList);
    List<TestCaseRunEntity> toEntityList(List<TestCaseRun> domainList);
}
