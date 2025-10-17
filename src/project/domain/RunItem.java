import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

package project.domain;


@Entity
@Table(name = "run_item")
public class RunItem {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID runId;

    @Column(nullable = false)
    private UUID testId;

    @Column
    private String paramsKey;

    @Column(nullable = false)
    private RunStatus status;

    @Column
    private int attempt;

    @Column
    private long durationMs;

    @Column
    private String workerId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant startedAt;

    @Column
    private Instant finishedAt;

}