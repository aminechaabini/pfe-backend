package com.example.demo.core.api.mapper;

import com.example.demo.core.api.dto.response.project.ProjectResponse;
import com.example.demo.core.domain.project.Project;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper: Project (domain) → ProjectResponse (API DTO).
 */
@Mapper(componentModel = "spring")
public interface ProjectResponseMapper {

    @Mapping(target = "testSuiteCount", ignore = true)
    @Mapping(target = "specSourceCount", ignore = true)
    ProjectResponse toResponse(Project domain);

    List<ProjectResponse> toResponseList(List<Project> domains);

    @AfterMapping
    default void setCounts(@MappingTarget ProjectResponse.Builder response, Project domain) {
        // MapStruct will generate a builder for records
        // We'll set counts from domain collections
    }

    /**
     * Manual implementation since records don't have setters.
     * MapStruct handles this automatically with records.
     */
    default ProjectResponse toResponseWithCounts(Project domain) {
        if (domain == null) {
            return null;
        }

        return new ProjectResponse(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getVariables(),
                domain.getTestSuites().size(),
                domain.getSpecSources().size(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
