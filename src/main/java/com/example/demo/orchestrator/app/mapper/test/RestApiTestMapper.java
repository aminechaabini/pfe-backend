package com.example.demo.orchestrator.app.mapper.test;

import com.example.demo.orchestrator.persistence.test.RESTAPITest;
import com.example.demo.orchestrator.persistence.test.TestSuite;
import com.example.demo.orchestrator.dto.test.CreateRestApiTestRequest;
import com.example.demo.orchestrator.dto.test.RestApiTestResponse;
import com.example.demo.orchestrator.dto.test.UpdateRestApiTestRequest;
import org.springframework.stereotype.Component;

@Component
public final class RestApiTestMapper {
  private RestApiTestMapper() {}

  /** Request -> New Entity (requires loaded TestSuite) */
  public static RESTAPITest fromCreate(CreateRestApiTestRequest dto, TestSuite suite) {
    return new RESTAPITest(
        suite,
        dto.name(),
        dto.description(),
        dto.httpMethod(),
        dto.url(),
        dto.headersJson(),
        dto.queryJson(),
        dto.body(),
        dto.assertionsJson()
    );
  }

  /** Apply update fields to existing REST test */
  public static void applyUpdate(RESTAPITest entity, UpdateRestApiTestRequest dto) {
    if (dto.name() != null) entity.setName(dto.name());
    if (dto.description() != null) entity.setDescription(dto.description());
    if (dto.httpMethod() != null) entity.setHttpMethod(dto.httpMethod());
    if (dto.url() != null) entity.setUrl(dto.url());
    if (dto.headersJson() != null) entity.setHeadersJson(dto.headersJson());
    if (dto.queryJson() != null) entity.setQueryJson(dto.queryJson());
    if (dto.body() != null) entity.setBody(dto.body());
    if (dto.assertionsJson() != null) entity.setAssertionsJson(dto.assertionsJson());
    // updatedAt is handled by entity @PreUpdate
  }

  /** Entity -> Response DTO */
  public static RestApiTestResponse toResponse(RESTAPITest entity) {
    return new RestApiTestResponse(
        entity.getId(),
        entity.getSuite().getId(),
        entity.getName(),
        entity.getDescription(),
        entity.getHttpMethod(),
        entity.getUrl(),
        entity.getHeadersJson(),
        entity.getQueryJson(),
        entity.getBody(),
        entity.getAssertionsJson(),
        entity.getCreatedAt(),
        entity.getUpdatedAt()
    );
  }
}
