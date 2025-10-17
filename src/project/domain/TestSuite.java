package project.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "test_suites")
public class TestSuite {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name="name", nullable=false, length=255, unique=true)
    private String name;

    @Column(name="description", nullable=true, length=255, unique=false)
    private String description;

    @Column(name="createdAt", nullable=false, length=255, unique=false)
    private Timestamp createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id") // FK column here
    private Project project;

}
