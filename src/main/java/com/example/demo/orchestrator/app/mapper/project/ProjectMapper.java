package com.example.demo.orchestrator.app.mapper.project;

import com.example.demo.orchestrator.domain.project.Project;
import com.example.demo.orchestrator.dto.project.CreateProjectRequest;
import com.example.demo.orchestrator.dto.project.ProjectResponse;
import com.example.demo.orchestrator.dto.project.UpdateProjectRequest;
import org.springframework.stereotype.Component;

@Component
public final class ProjectMapper {
  private ProjectMapper() {}

  /** Request -> New Entity (uses Project.create factory) */
  public static Project fromCreate(CreateProjectRequest dto) {
    return Project.create(dto.name(), dto.description());
  }

  /** Apply update fields to an existing entity */
  public static void applyUpdate(Project entity, UpdateProjectRequest dto) {
    if (dto.name() != null) {
      entity.rename(dto.name());
    }
    if (dto.description() != null) {
      entity.changeDescription(dto.description());
    }
    // If you expose optimistic locking at API edge, verify dto.version() vs entity.getVersion() in service.
  }

  /** Entity -> Response DTO */
  public ProjectResponse toResponse(Project entity) {
    return new ProjectResponse(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }
}
