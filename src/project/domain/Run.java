
@Entity
@Table(name = "runs")
public class Run {
    @Id
    @GeneratedValue
    private final UUID id;

    @Column(name="type", nullable=false, length=50, unique=false)
    private final RunType type;

    @Column(name="project_id", nullable=false, length=255, unique=false)
    private final UUID projectId;

    @Column(name="test_suite_id", nullable=true, length=255, unique=false)
    private final UUID testSuiteId; // nullable if type is TEST

    @Column(name="status", nullable=false, length=50, unique=false)
    private final RunStatus status;

    @Column(name="result", nullable=false, length=50, unique=false)
    private final RunResult result;

    @Column(name="createdAt", nullable=false, length=50, unique=false)
    private final Timestamp createdAt;

    @Column(name="startedAt", nullable=false, length=255, unique=false)
    private final Timestamp startedAt;

    @Column(name="completedAt", nullable=true, length=255, unique=false)
    private final Timestamp completedAt;

    @Column(name="run_status", nullable=false, length=50, unique=false)
    private final RunStatus status;

}
