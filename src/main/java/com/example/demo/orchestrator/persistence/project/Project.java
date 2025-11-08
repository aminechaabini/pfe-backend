package com.example.demo.orchestrator.persistence.project;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name = "projects", schema = "app")
public class Project {

    @Id
    @SequenceGenerator(
            name = "project_seq",                 // JPA generator name (local to this entity)
            sequenceName = "app.project_seq",     // actual DB sequence (include schema!)
            allocationSize = 50                   // fetch 50 ids at once (perf)
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_seq")
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 255)
    @Column(name="name", nullable=false, length=255, unique=true)
    private String name;

    @Size(max = 1000)
    @Column(name="description", nullable=true, length=255, unique=false)
    private String description;

    @Column(name="createdAt", nullable=false, length=255, unique=false)
    private Instant createdAt;

    @Column(name="updatedAt", nullable=false)
    private Instant updatedAt;

    protected Project() {
        // for JPA
    }
}
