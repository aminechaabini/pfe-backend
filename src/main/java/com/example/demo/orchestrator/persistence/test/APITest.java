package com.example.demo.orchestrator.persistence.test;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
@Table(name = "tests")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "test_kind") // lets us query polymorphically and know the concrete type
public abstract class APITest implements Test {

    @Id
    @SequenceGenerator(
            name = "api_test_seq",                 // JPA generator name (local to this entity)
            sequenceName = "app.api_test_seq",     // actual DB sequence (include schema!)
            allocationSize = 50                   // fetch 50 ids at once (perf)
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "api_test_seq")
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "suite_id", nullable = false)
    private TestSuite suite;


    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected APITest() {}

    protected APITest(TestSuite suite, String name, String description) {
        this.suite = suite;
        this.name = name;
        this.description = description;
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

    // Getters/setters kept minimal to match a typical zip-style POJO setup.
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TestSuite getSuite() { return suite; }
    public void setSuite(TestSuite suite) { this.suite = suite; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
