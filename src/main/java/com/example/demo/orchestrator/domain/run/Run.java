
package com.example.demo.orchestrator.domain.run;

import com.example.demo.orchestrator.domain.test.APITest;
import com.example.demo.orchestrator.domain.test.TestSuite;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "runs")
public class Run {

  // --- Identity: switch to Long + SEQUENCE (consistent with other entities) ---
  @Id
  @SequenceGenerator(
          name = "run_seq",                 // JPA generator name (local to this entity)
          sequenceName = "app.run_seq",     // actual DB sequence (include schema!)
          allocationSize = 50                   // fetch 50 ids at once (perf)
  )
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "run_seq")
  private Long id;

  // --- Target: exactly one of these should be non-null (enforce in service layer) ---
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "runnable_id")
  private Runnable runnable;

  // --- Type/Status/Result: mapped as strings for stability ---
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private RunType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private RunStatus status = RunStatus.NOT_STARTED;

  @Enumerated(EnumType.STRING)
  @Column(name = "result")
  private RunResult result; // null until completed/failed

  // --- Timing & audit: use Instant and snake_case column names ---
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "started_at")
  private Instant startedAt;

  @Column(name = "completed_at")
  private Instant completedAt;

  // --- Optimistic lock ---

  protected Run() {}

  private Run(Runnable runnable) {
    this.runnable = runnable;
    this.type = runnable instanceof TestSuite ? RunType.SUITE : RunType.TEST;
    this.status = RunStatus.NOT_STARTED;
  }


  @PrePersist
  void onCreate() {
    Instant now = Instant.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  void onUpdate() {
    this.updatedAt = Instant.now();
  }

  // -------------------- Getters/Setters --------------------
  public Long getId() { return id; }

    public Runnable getRunnable() {
        return runnable;
    }

    public RunType getType() {
        return type;
    }
  public void setType(RunType type) { this.type = type; }

  public RunStatus getStatus() { return status; }
  public void setStatus(RunStatus status) { this.status = status; }

  public RunResult getResult() { return result; }
  public void setResult(RunResult result) { this.result = result; }

  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public Instant getStartedAt() { return startedAt; }
  public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

  public Instant getCompletedAt() { return completedAt; }
  public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

  // keep equals/hashCode if you need them; omitted here to match your zip style
}
