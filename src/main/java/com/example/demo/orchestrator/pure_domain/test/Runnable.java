package com.example.demo.orchestrator.pure_domain.test;

import java.time.Instant;

public abstract class Runnable {
    private Long id;
    private String name;
    private String description;
    protected Instant createdAt;
    protected Instant updatedAt;
    private RunnableType type;

    public Runnable(Long id, String name, String description, RunnableType type) {
        this.id = id;
        setName(name);
        setDescription(description);
        this.type = type;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public RunnableType getType() { return type; }

    public void setName(String name){
        String trimmed = name.trim();
        if (trimmed.isBlank()) throw new IllegalArgumentException("name required");
        if (trimmed.length() > 200) throw new IllegalArgumentException("name too long");
        if (trimmed.equals(this.name)) return;
        this.name = trimmed;
    }

    public void setDescription(String description){
        String trimmed = description.trim();
        if (trimmed.length() > 2000) throw new IllegalArgumentException("description too long");
        if (trimmed.equals(this.description)) return;
        this.description = trimmed;
    }

    public void setType(String type){
        this.type = RunnableType.valueOf(type);
        this.updatedAt = Instant.now();
    }

}
