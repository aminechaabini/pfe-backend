package com.example.demo.orchestrator.persistence.test;

import com.example.demo.orchestrator.persistence.project.Project;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "test_suites", schema = "app")
public class TestSuite{

    @Id
    @SequenceGenerator(
            name = "test_suite_seq",                 // JPA generator name (local to this entity)
            sequenceName = "app.test_suite_seq",     // actual DB sequence (include schema!)
            allocationSize = 50                   // fetch 50 ids at once (perf)
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_suite_seq")
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name="name", nullable=false, length=255, unique=true)
    private String name;

    @Size(max = 1000)
    @Column(name="description", nullable=true, length=255, unique=false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id") // FK column here
    private Project project;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected TestSuite() {
        // for JPA
    }
}
