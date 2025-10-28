package com.example.demo.orchestrator.domain.run;

import com.example.demo.orchestrator.domain.test.APITest;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "run_items", schema = "app")
public class RunItem {

  // --- Identity: Long + SEQUENCE ---
  @Id
  @SequenceGenerator(
          name = "run_item_seq",                 // JPA generator name (local to this entity)
          sequenceName = "app.run_item_seq",     // actual DB sequence (include schema!)
          allocationSize = 50                   // fetch 50 ids at once (perf)
  )
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "run_item_seq")
  private Long id;

  // --- Parent run (required) ---
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "run_id", nullable = false)
  private Run run;

  // --- Executed test (required) ---
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "test_id", nullable = false)
  private APITest test;

  // --- State ---
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private RunStatus status = RunStatus.NOT_STARTED;

  @Enumerated(EnumType.STRING)
  @Column(name = "result")
  private RunResult result; // null until done

  // --- Timing ---
  @Column(name = "started_at")
  private Instant startedAt;

  @Column(name = "finished_at")
  private Instant finishedAt;

  @Column(name = "duration_ms")
  private Long durationMs; // optional denormalized value

  // --- Worker metadata (optional) ---
  @Column(name = "worker_id", length = 255)
  private String workerId;

  // --- Audit ---
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  // --- Optimistic lock (optional but useful) ---
  @Version
  @Column(name = "version", nullable = false)
  private long version = 0L;

  protected RunItem() {}

  // -------------------- JPA lifecycle --------------------
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

  public Run getRun() { return run; }
  public void setRun(Run run) { this.run = run; }

  public APITest getTest() { return test; }
  public void setTest(APITest test) { this.test = test; }

  public RunStatus getStatus() { return status; }
  public void setStatus(RunStatus status) { this.status = status; }

  public RunResult getResult() { return result; }
  public void setResult(RunResult result) { this.result = result; }

  public Instant getStartedAt() { return startedAt; }
  public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

  public Instant getFinishedAt() { return finishedAt; }
  public void setFinishedAt(Instant finishedAt) { this.finishedAt = finishedAt; }

  public Long getDurationMs() { return durationMs; }
  public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

  public String getWorkerId() { return workerId; }
  public void setWorkerId(String workerId) { this.workerId = workerId; }

  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }

  public long getVersion() { return version; }
}
