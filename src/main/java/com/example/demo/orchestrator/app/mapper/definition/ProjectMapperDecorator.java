package com.example.demo.orchestrator.app.mapper.definition;

import com.example.demo.orchestrator.app.mapper.config.CycleAvoidingMappingContext;
import com.example.demo.orchestrator.domain.project.Project;
import com.example.demo.orchestrator.domain.test.test_suite.TestSuite;
import com.example.demo.orchestrator.persistence.entity.project.ProjectEntity;
import com.example.demo.orchestrator.persistence.entity.test.TestSuiteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Decorator for ProjectMapper to handle:
 * 1. Factory method Project.create() instead of constructor
 * 2. Circular references (Project ↔ TestSuite)
 *
 * The decorator pattern allows us to customize the mapping logic
 * while letting MapStruct handle the simple field mappings.
 */
public abstract class ProjectMapperDecorator implements ProjectMapper {

    @Autowired
    @Qualifier("delegate")
    private ProjectMapper delegate;

    @Autowired
    private TestSuiteMapper testSuiteMapper;

    @Override
    public Project toDomain(ProjectEntity entity, CycleAvoidingMappingContext context) {
        // Check if already mapped (avoid infinite loop)
        Project existing = context.getMappedInstance(entity, Project.class);
        if (existing != null) {
            return existing;
        }

        // Use factory method Project.create() instead of constructor
        Project domain = Project.create(entity.getName(), entity.getDescription());

        // Set ID
        if (entity.getId() != null) {
            domain.setId(entity.getId());
        }

        // Store in context BEFORE mapping children (breaks circular reference)
        context.storeMappedInstance(entity, domain);

        // Map variables
        if (entity.getVariables() != null) {
            entity.getVariables().forEach(domain::setVariable);
        }

        // Map test suites
        if (entity.getTestSuites() != null) {
            for (TestSuiteEntity suiteEntity : entity.getTestSuites()) {
                TestSuite suite = testSuiteMapper.toDomain(suiteEntity, context);
                domain.addSuite(suite);
            }
        }

        return domain;
    }

    @Override
    public ProjectEntity toEntity(Project domain, CycleAvoidingMappingContext context) {
        // Check if already mapped (avoid infinite loop)
        ProjectEntity existing = context.getMappedInstance(domain, ProjectEntity.class);
        if (existing != null) {
            return existing;
        }

        // Create entity
        ProjectEntity entity = new ProjectEntity(domain.getName(), domain.getDescription());

        // Set ID
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }

        // Store in context BEFORE mapping children (breaks circular reference)
        context.storeMappedInstance(domain, entity);

        // Map variables
        entity.setVariables(new java.util.HashMap<>(domain.getVariables()));

        // Map test suites
        if (domain.getTestSuites() != null) {
            for (TestSuite suite : domain.getTestSuites()) {
                TestSuiteEntity suiteEntity = testSuiteMapper.toEntity(suite, context);
                entity.addTestSuite(suiteEntity);  // Use helper method to maintain bidirectional relationship
            }
        }

        return entity;
    }
}
