package com.example.demo.core.infrastructure.persistence.mapper;


import com.example.demo.core.domain.test.TestCase;
import com.example.demo.core.domain.test.api.RestApiTest;
import com.example.demo.core.domain.test.api.SoapApiTest;
import com.example.demo.core.domain.test.e2e.E2eTest;
import com.example.demo.core.infrastructure.persistence.entity.test.E2eTestEntity;
import com.example.demo.core.infrastructure.persistence.entity.test.RestApiTestEntity;
import com.example.demo.core.infrastructure.persistence.entity.test.SoapApiTestEntity;
import com.example.demo.core.infrastructure.persistence.entity.test.TestCaseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {E2eStepMapper.class, JsonConverter.class, AssertionMapper.class})
public abstract class TestCaseMapper {

    protected JsonConverter jsonConverter;
    protected AssertionMapper assertionMapper;

    // ===============================
    // ENTITY → DOMAIN (toDomain)
    // ===============================
    // Polymorphic mapping - manual dispatching to concrete methods
    public TestCase toDomain(TestCaseEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof RestApiTestEntity) {
            return toDomain((RestApiTestEntity) entity);
        } else if (entity instanceof SoapApiTestEntity) {
            return toDomain((SoapApiTestEntity) entity);
        } else if (entity instanceof E2eTestEntity) {
            return toDomain((E2eTestEntity) entity);
        }
        throw new IllegalArgumentException("Unknown entity type: " + entity.getClass());
    }

    public abstract E2eTest toDomain(E2eTestEntity entity);

    public RestApiTest toDomain(RestApiTestEntity entity) {
        if (entity == null) {
            return null;
        }
        return RestApiTest.reconstitute(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            jsonConverter.jsonToRestRequest(entity.getRequestJson()),
            assertionMapper.toDomainList(entity.getAssertions()),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public SoapApiTest toDomain(SoapApiTestEntity entity) {
        if (entity == null) {
            return null;
        }
        return SoapApiTest.reconstitute(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            jsonConverter.jsonToSoapRequest(entity.getRequestJson()),
            assertionMapper.toDomainList(entity.getAssertions()),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public abstract List<TestCase> toDomainList(List<TestCaseEntity> entities);


    // ===============================
    // DOMAIN → ENTITY (toEntity)
    // ===============================
    // Polymorphic mapping to entity - manual dispatching to concrete methods
    public TestCaseEntity toEntity(TestCase domain) {
        if (domain == null) {
            return null;
        }
        if (domain instanceof RestApiTest) {
            return toEntity((RestApiTest) domain);
        } else if (domain instanceof SoapApiTest) {
            return toEntity((SoapApiTest) domain);
        } else if (domain instanceof E2eTest) {
            return toEntity((E2eTest) domain);
        }
        throw new IllegalArgumentException("Unknown test case type: " + domain.getClass());
    }

    public abstract E2eTestEntity toEntity(E2eTest domain);

    @Mapping(target = "requestJson", expression = "java(jsonConverter.restRequestToJson(domain.getRequest()))")
    @Mapping(target = "assertions", expression = "java(assertionMapper.toEntityList(domain.getAssertions()))")
    public abstract RestApiTestEntity toEntity(RestApiTest domain);

    @Mapping(target = "requestJson", expression = "java(jsonConverter.soapRequestToJson(domain.getRequest()))")
    @Mapping(target = "assertions", expression = "java(assertionMapper.toEntityList(domain.getAssertions()))")
    public abstract SoapApiTestEntity toEntity(SoapApiTest domain);

    public abstract List<TestCaseEntity> toEntityList(List<TestCase> domains);

    // ===============================
    // UPDATE ENTITY FROM DOMAIN
    // ===============================
    // Polymorphic update - manual dispatching to concrete methods
    public void updateEntityFromDomain(@MappingTarget TestCaseEntity entity, TestCase domain) {
        if (entity == null || domain == null) {
            return;
        }
        if (entity instanceof RestApiTestEntity && domain instanceof RestApiTest) {
            updateEntityFromDomain((RestApiTestEntity) entity, (RestApiTest) domain);
        } else if (entity instanceof SoapApiTestEntity && domain instanceof SoapApiTest) {
            updateEntityFromDomain((SoapApiTestEntity) entity, (SoapApiTest) domain);
        } else if (entity instanceof E2eTestEntity && domain instanceof E2eTest) {
            updateEntityFromDomain((E2eTestEntity) entity, (E2eTest) domain);
        } else {
            throw new IllegalArgumentException("Entity type " + entity.getClass() +
                " does not match domain type " + domain.getClass());
        }
    }

    public abstract void updateEntityFromDomain(@MappingTarget E2eTestEntity entity, E2eTest domain);

    @Mapping(target = "requestJson", expression = "java(jsonConverter.restRequestToJson(domain.getRequest()))")
    @Mapping(target = "assertions", expression = "java(assertionMapper.toEntityList(domain.getAssertions()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract void updateEntityFromDomain(@MappingTarget RestApiTestEntity entity, RestApiTest domain);

    @Mapping(target = "requestJson", expression = "java(jsonConverter.soapRequestToJson(domain.getRequest()))")
    @Mapping(target = "assertions", expression = "java(assertionMapper.toEntityList(domain.getAssertions()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract void updateEntityFromDomain(@MappingTarget SoapApiTestEntity entity, SoapApiTest domain);
}
