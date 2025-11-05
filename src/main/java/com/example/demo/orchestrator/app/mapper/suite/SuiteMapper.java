package com.example.demo.orchestrator.app.mapper.suite;

import com.example.demo.orchestrator.persistence.project.Project;
import com.example.demo.orchestrator.persistence.test.TestSuite;
import com.example.demo.orchestrator.dto.suite.CreateTestSuiteRequest;
import com.example.demo.orchestrator.dto.suite.TestSuiteResponse;
import com.example.demo.orchestrator.dto.suite.UpdateTestSuiteRequest;
import org.springframework.stereotype.Component;

@Component
public final class SuiteMapper {
  private SuiteMapper() {}

  /** Request -> New Entity (requires loaded Project) */
  public static TestSuite fromCreate(CreateTestSuiteRequest dto, Project project) {
    return TestSuite.create(project, dto.name(), dto.description());
  }

  /** Apply update fields to an existing suite */
  public static void applyUpdate(TestSuite entity, UpdateTestSuiteRequest dto) {
    if (dto.name() != null) {
      entity.rename(dto.name());
    }
    if (dto.description() != null) {
      entity.changeDescription(dto.description());
    }
  }

  /** Entity -> Response DTO */
  public static TestSuiteResponse toResponse(TestSuite entity) {
    return new TestSuiteResponse(
        entity.getId(),
        entity.getProject().getId(),
        entity.getName(),
        entity.getDescription(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }
}
