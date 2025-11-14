package com.example.demo.orchestrator.app.mapper.definition;

import com.example.demo.orchestrator.app.mapper.config.CycleAvoidingMappingContext;
import com.example.demo.orchestrator.domain.test.TestCase;
import com.example.demo.orchestrator.domain.test.test_suite.TestSuite;
import com.example.demo.orchestrator.persistence.entity.test.TestCaseEntity;
import com.example.demo.orchestrator.persistence.entity.test.TestSuiteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Decorator for TestSuiteMapper to handle:
 * 1. Circular references (TestSuite ↔ Project)
 * 2. TestCase collection mapping
 *
 * The decorator pattern allows us to customize the mapping logic
 * while letting MapStruct handle the simple field mappings.
 */
public abstract class TestSuiteMapperDecorator implements TestSuiteMapper {

    @Autowired
    @Qualifier("delegate")
    private TestSuiteMapper delegate;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Override
    public TestSuite toDomain(TestSuiteEntity entity, CycleAvoidingMappingContext context) {
        // Check if already mapped (avoid infinite loop)
        TestSuite existing = context.getMappedInstance(entity, TestSuite.class);
        if (existing != null) {
            return existing;
        }

        // Use TestSuite constructor (not a factory method)
        TestSuite domain = new TestSuite(entity.getName(), entity.getDescription());

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

        // Map test cases
        if (entity.getTestCases() != null) {
            for (TestCaseEntity testCaseEntity : entity.getTestCases()) {
                TestCase testCase = testCaseMapper.toDomain(testCaseEntity);
                domain.addTestCase(testCase);
            }
        }

        // Note: We don't map projects here to avoid circular reference
        // Projects should be loaded separately by the service layer if needed

        return domain;
    }

    @Override
    public TestSuiteEntity toEntity(TestSuite domain, CycleAvoidingMappingContext context) {
        // Check if already mapped (avoid infinite loop)
        TestSuiteEntity existing = context.getMappedInstance(domain, TestSuiteEntity.class);
        if (existing != null) {
            return existing;
        }

        // Create entity
        TestSuiteEntity entity = new TestSuiteEntity(domain.getName(), domain.getDescription());

        // Set ID
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }

        // Store in context BEFORE mapping children (breaks circular reference)
        context.storeMappedInstance(domain, entity);

        // Map variables
        entity.setVariables(new java.util.HashMap<>(domain.getVariables()));

        // Map test cases
        if (domain.getTestCases() != null) {
            for (TestCase testCase : domain.getTestCases()) {
                TestCaseEntity testCaseEntity = testCaseMapper.toEntity(testCase);
                entity.addTestCase(testCaseEntity);
            }
        }

        // Note: We don't map projects in the suite->entity direction
        // Project relationships are managed by the ProjectMapper

        return entity;
    }
}
