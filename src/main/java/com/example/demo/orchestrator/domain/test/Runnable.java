package com.example.demo.orchestrator.domain.test;

import java.time.Instant;

public abstract class Runnable {
    private Long id;
    private String name;
    private String description;
    protected Instant createdAt;
    protected Instant updatedAt;
    private RunnableType type;

    public Runnable(String name, String description, String type) {
        validateName(name.trim());
        this.name=name.trim();
        validateDescription(description);
        this.description=description.trim();
        this.type = RunnableType.valueOf(type);
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public RunnableType getType() { return type; }

    public void rename(String newName){
        String trimmed = newName.trim();
        validateName(trimmed);
        if (trimmed.equals(this.name)) return;
        this.name = trimmed;
        touch();
    }

    public void updateDescription(String newDescription){
        String trimmed = newDescription.trim();
        validateDescription(trimmed);
        if (trimmed.equals(this.name)) return;
        this.description = trimmed;
        touch();
    }

    protected void validateName(String name) {
        if (name == null) throw new IllegalArgumentException("name must not be null");
        else if (name.isEmpty()) throw new IllegalArgumentException("name must not be blank");
        else if (name.length() > 40) throw new IllegalArgumentException("name must be at most 40 characters");
    }

    protected void validateDescription(String description){
        if (description.length() > 2000) throw new IllegalArgumentException("description too long");
    }
    protected void touch(){
        this.updatedAt=Instant.now();
    }

}
