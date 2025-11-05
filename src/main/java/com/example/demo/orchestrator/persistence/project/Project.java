package com.example.demo.orchestrator.persistence.project;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name = "projects", schema = "app")
public class Project {

    //fields

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

    //


    @Column(name="createdAt", nullable=false, length=255, unique=false)
    private Instant createdAt;

    @Column(name="updatedAt", nullable=false)
    private Instant updatedAt;

    //

    protected Project() {
        // for JPA
    }

    private Project(String name, String description) {
        this.name = name;
        this.description = description;
    }

     // --- Factories (optional but recommended) ---
    public static Project create(String name) {
        return new Project(name, null);
    }
    public static Project create(String name, String description) {
        return new Project(name, description);
    }

    // --- Domain behavior (no public setters) ---
  public void rename(String newName) {
    this.name = sanitizeName(newName);
    touch();
  }

  public void changeDescription(String newDescription) {
    this.description = sanitizeDescription(newDescription);
    touch();
  }

  // --- JPA lifecycle ---
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

  private void touch() {
    this.updatedAt = Instant.now();
  }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
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
