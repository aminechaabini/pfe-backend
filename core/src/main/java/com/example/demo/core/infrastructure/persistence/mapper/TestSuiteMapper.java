package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.application.service.ProjectService;
import com.example.demo.core.domain.test.test_suite.TestSuite;
import com.example.demo.core.infrastructure.persistence.entity.project.ProjectEntity;
import com.example.demo.core.infrastructure.persistence.entity.spec.EndpointEntity;
import com.example.demo.core.infrastructure.persistence.entity.test.TestSuiteEntity;
import com.example.demo.core.infrastructure.persistence.jpa.EndpointRepository;
import com.example.demo.core.infrastructure.persistence.jpa.ProjectRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MapStruct mapper for TestSuite ↔ TestSuiteEntity.
 *
 * Note: TestCases collection should be loaded separately to avoid circular references.
 */
@Mapper(componentModel = "spring", uses = {EndpointMapper.class, TestCaseMapper.class, ProjectMapper.class})
public abstract class TestSuiteMapper {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    EndpointRepository endpointRepository;

    /**
     * Convert entity to domain using reconstitution.
     * Preserves full entity state including identity and timestamps.
     */
    public TestSuite toDomain(TestSuiteEntity entity) {
        if (entity == null) {
            return null;
        }

        return TestSuite.reconstitute(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getVariables(),
            entity.getProject() != null ? entity.getProject().getId() : null,
            entity.getEndpoint() != null ? entity.getEndpoint().getId() : null,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Convert domain to entity.
     */
    @Mapping(target = "project", expression = "java(mapProjectEntityFromId(domain.getProjectId()))")
    @Mapping(target = "endpoint", expression = "java(mapEndpointEntityFromId(domain.getEndpointId()))")
    public abstract TestSuiteEntity toEntity(TestSuite domain);


    ProjectEntity mapProjectEntityFromId(Long id) {
        if (id == null) {
            return null;
        }
        return projectRepository.findById(id).orElse(null);
    }

    EndpointEntity mapEndpointEntityFromId(Long id) {
        if (id == null) {
            return null;
        }
        return endpointRepository.findById(id).orElse(null);
    }


    /**
     * Update existing entity from domain.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "endpoint", ignore = true)  // Set by repository before save
    public abstract void updateEntityFromDomain(@MappingTarget TestSuiteEntity entity, TestSuite domain);

    }
