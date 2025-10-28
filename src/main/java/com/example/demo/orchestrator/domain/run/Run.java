
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
  @JoinColumn(name = "suite_id")
  private TestSuite suite;   // nullable

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id")
  private APITest test;      // nullable

  // --- Type/Status/Result: mapped as strings for stability ---
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private RunType type = RunType.SUITE; // default; set to TEST when test != null

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
  @Version
  @Column(name = "version", nullable = false)
  private long version = 0L;

  // --- Items within this run ---
  @OneToMany(mappedBy = "run", orphanRemoval = false)
  private List<RunItem> items = new ArrayList<>();

  protected Run() {}

  private Run(TestSuite suite) {
    this.suite = suite;
    this.type = RunType.SUITE;
    this.status = RunStatus.NOT_STARTED;
  }

  private Run(APITest test) {
    this.test = test;
    this.type = RunType.TEST;
    this.status = RunStatus.NOT_STARTED;
  }

  
  // -------------------- Factories --------------------

  public static Run forSuite(TestSuite suite) {
    return new Run(suite);
  }

  public static Run forTest(APITest test) {
    return new Run(test);
  }

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

  public TestSuite getSuite() { return suite; }
  public void setSuite(TestSuite suite) { this.suite = suite; }

  public APITest getTest() { return test; }
  public void setTest(APITest test) { this.test = test; }

  public RunType getType() { return type; }
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

  public long getVersion() { return version; }

  public List<RunItem> getItems() { return items; }

  // keep equals/hashCode if you need them; omitted here to match your zip style
}
