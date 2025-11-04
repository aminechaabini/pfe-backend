package com.example.demo.orchestrator.domain.test;

import com.example.demo.orchestrator.domain.project.Project;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "test_suites", schema = "app")
public class TestSuite implements Runnable{

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

    private TestSuite(String name, String description, Project project) {
        this.name = name;
        this.description = description;
        this.project = project;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

     // --- Factories (optional but recommended) ---    

    public static TestSuite create(Project project, String name) {
        return new TestSuite(name, null, project);
    }

    public static TestSuite create(Project project, String name, String description) {
        return new TestSuite(name, description, project);
    }

    public void rename(String newName) {
      this.name = sanitizeName(newName);
      this.updatedAt = Instant.now();
    }

    public void changeDescription(String newDescription) {
      this.description = sanitizeDescription(newDescription);
      this.updatedAt = Instant.now();
    }
    
  public Long getId() { return id; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public Project getProject() { return project; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }

    private static String sanitizeName(String s) {
    if (s == null) throw new IllegalArgumentException("name cannot be null");
    String v = s.trim().replaceAll("\\s+", " ");
    if (v.isEmpty()) throw new IllegalArgumentException("name cannot be blank");
    if (v.length() > 255) throw new IllegalArgumentException("name too long (max 255)");
    return v;
  }

  private static String sanitizeDescription(String s) {
    if (s == null) return null;
    String v = s.trim();
    if (v.length() > 1000) throw new IllegalArgumentException("description too long (max 1000)");
    return v;
  }

}
