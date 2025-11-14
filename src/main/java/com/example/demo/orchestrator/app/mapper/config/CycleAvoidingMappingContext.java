package com.example.demo.orchestrator.app.mapper.config;

import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Component;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Context object for handling circular references during mapping.
 *
 * Problem:
 * - Project has many TestSuites
 * - TestSuite has many Projects
 * This bidirectional many-to-many relationship causes infinite loops during mapping.
 *
 * Solution:
 * This class tracks already-mapped instances using an IdentityHashMap.
 * When MapStruct encounters an object that's already been mapped, it returns
 * the cached instance instead of mapping it again.
 *
 * Usage:
 * Add @Context CycleAvoidingMappingContext to mapper method signatures:
 *
 * <pre>
 * {@code
 * @Mapper
 * public interface ProjectMapper {
 *     Project toDomain(ProjectEntity entity, @Context CycleAvoidingMappingContext context);
 * }
 * }
 * </pre>
 *
 * MapStruct will automatically call the @BeforeMapping methods before each mapping operation.
 */
@Component
public class CycleAvoidingMappingContext {

    /**
     * Cache of already-mapped instances.
     * Uses IdentityHashMap to compare object identity (not equals()).
     */
    private final Map<Object, Object> knownInstances = new IdentityHashMap<>();

    /**
     * Check if an object has already been mapped.
     * If it has, return the cached mapped instance.
     *
     * Called automatically by MapStruct before each mapping operation.
     */
    @BeforeMapping
    public <T> T getMappedInstance(Object source, @TargetType Class<T> targetType) {
        @SuppressWarnings("unchecked")
        T mappedInstance = (T) knownInstances.get(source);
        return mappedInstance;
    }

    /**
     * Store a mapping between source and target objects.
     * This prevents infinite loops when the same object is encountered again.
     *
     * Called automatically by MapStruct before each mapping operation.
     */
    @BeforeMapping
    public void storeMappedInstance(Object source, @MappingTarget Object target) {
        knownInstances.put(source, target);
    }

    /**
     * Clear the cache.
     * Should be called after completing a full mapping operation tree.
     */
    public void clear() {
        knownInstances.clear();
    }
}
