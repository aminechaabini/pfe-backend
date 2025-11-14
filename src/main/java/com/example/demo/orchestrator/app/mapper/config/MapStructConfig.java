package com.example.demo.orchestrator.app.mapper.config;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Global MapStruct configuration for all mappers in the application.
 *
 * Configuration:
 * - componentModel = "spring": Enables Spring dependency injection for mappers
 * - unmappedTargetPolicy = ERROR: Fail compilation if any target field is not mapped (prevents bugs)
 * - nullValuePropertyMappingStrategy = IGNORE: Skip null values during mapping
 * - collectionMappingStrategy = ADDER_PREFERRED: Use adder methods for collections when available
 */
@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface MapStructConfig {
}
