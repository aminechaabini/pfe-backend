package com.example.demo.orchestrator.app.mapper.run;

import com.example.demo.orchestrator.persistence.run.Run;
import com.example.demo.orchestrator.persistence.run.RunResult;
import com.example.demo.orchestrator.persistence.run.RunStatus;
import com.example.demo.orchestrator.persistence.test.APITest;
import com.example.demo.orchestrator.persistence.test.TestSuite;
import com.example.demo.orchestrator.dto.run.CreateRunRequest;
import com.example.demo.orchestrator.dto.run.RunItemResponse;
import com.example.demo.orchestrator.dto.run.RunResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public final class RunMapper {
  private RunMapper() {}

  /** Request -> New Entity (requires either loaded suite OR loaded test; pass null for the other) */
  public static Run fromCreate(CreateRunRequest dto, TestSuite suite, APITest test) {
    if (suite != null && test == null) {
      return Run.forSuite(suite);
    } else if (suite == null && test != null) {
      return Run.forTest(test);
    } else {
      throw new IllegalArgumentException("Exactly one of suite or test must be non-null");
    }
  }

  /** Entity -> Response DTO (with items) */
  public static RunResponse toResponse(Run entity) {
    List<RunItemResponse> itemDtos = entity.getItems()
        .stream()
        .map(RunMapper::toItemResponse)
        .collect(Collectors.toList());

    Long suiteId = entity.getSuite() != null ? entity.getSuite().getId() : null;
    Long testId  = entity.getTest()  != null ? entity.getTest().getId()  : null;

    return new RunResponse(
        entity.getId(),
        entity.getType()   != null ? entity.getType().name()   : null,
        entity.getStatus() != null ? entity.getStatus().name() : null,
        entity.getResult() != null ? entity.getResult().name() : null,
        suiteId,
        testId,
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getStartedAt(),
        entity.getCompletedAt(),
        itemDtos
    );
  }

  /** RunItem -> Response DTO */
  public static RunItemResponse toItemResponse(RunItem item) {
    return new RunItemResponse(
        item.getId(),
        item.getRun()  != null ? item.getRun().getId()   : null,
        item.getTest() != null ? item.getTest().getId()  : null,
        item.getStatus() != null ? item.getStatus().name() : RunStatus.NOT_STARTED.name(),
        item.getResult() != null ? item.getResult().name() : null,
        item.getStartedAt(),
        item.getFinishedAt(),
        item.getDurationMs(),
        item.getWorkerId()
    );
  }

  /** Helper to set terminal state (optional; call from service when run completes) */
  public static void markCompleted(Run run, boolean success) {
    run.setStatus(RunStatus.COMPLETED);
    run.setResult(success ? RunResult.SUCCESS : RunResult.FAILURE);
  }
}
