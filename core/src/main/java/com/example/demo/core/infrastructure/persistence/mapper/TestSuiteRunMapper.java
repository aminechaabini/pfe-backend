package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.run.TestSuiteRun;
import com.example.demo.core.infrastructure.persistence.entity.run.TestSuiteRunEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper for TestSuiteRun ↔ TestSuiteRunEntity.
 *
 * Manual implementation due to complex state management and TestSuite reference handling.
 */
@Mapper(componentModel = "spring", uses = {TestSuiteMapper.class, TestCaseRunMapper.class})
public abstract class TestSuiteRunMapper {

    @Autowired
    protected TestSuiteMapper testSuiteMapper;

    @Autowired
    protected TestCaseRunMapper testCaseRunMapper;

    /**
     * Convert entity to domain.
     * Note: TestSuite is loaded separately to avoid N+1 queries.
     */
    public TestSuiteRun toDomain(TestSuiteRunEntity entity) {
        if (entity == null) {
            return null;
        }

        TestSuiteRun run = new TestSuiteRun();

        // Set ID via reflection (domain class has no setter)
        try {
            java.lang.reflect.Field idField = com.example.demo.core.domain.run.Run.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(run, entity.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID on TestSuiteRun", e);
        }

        // Map TestSuite reference
        if (entity.getTestSuite() != null) {
            run.setTestSuite(testSuiteMapper.toDomain(entity.getTestSuite()));
        }

        // Map test case runs
        if (entity.getTestCaseRuns() != null) {
            entity.getTestCaseRuns().forEach(testCaseRunEntity -> {
                run.addTestCaseRun(testCaseRunMapper.toDomain(testCaseRunEntity));
            });
        }

        // Copy state fields via reflection
        copyRunStateFromEntity(run, entity);

        return run;
    }

    /**
     * Convert domain to entity.
     */
    @Mapping(target = "testSuite", ignore = true)  // Set by repository
    @Mapping(target = "testCaseRuns", ignore = true)  // Mapped manually
    @Mapping(target = "errorMessage", ignore = true)  // Set during execution
    public abstract TestSuiteRunEntity toEntity(TestSuiteRun domain);

    /**
     * Update entity from domain.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "testSuite", ignore = true)  // Set by repository
    @Mapping(target = "testCaseRuns", ignore = true)  // Handled separately
    @Mapping(target = "errorMessage", ignore = true)  // Set during execution
    public abstract void updateEntityFromDomain(@MappingTarget TestSuiteRunEntity entity, TestSuiteRun domain);

    /**
     * Copy run state fields from entity to domain using reflection.
     */
    private void copyRunStateFromEntity(TestSuiteRun run, TestSuiteRunEntity entity) {
        try {
            Class<?> runClass = com.example.demo.core.domain.run.Run.class;

            setField(runClass, run, "status", entity.getStatus());
            setField(runClass, run, "result", entity.getResult());
            setField(runClass, run, "startedAt", entity.getStartedAt());
            setField(runClass, run, "completedAt", entity.getCompletedAt());
            setField(runClass, run, "updatedAt", entity.getUpdatedAt());
            setField(runClass, run, "createdAt", entity.getCreatedAt());
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy run state", e);
        }
    }

    /**
     * Helper to set field via reflection.
     */
    private void setField(Class<?> clazz, Object target, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
