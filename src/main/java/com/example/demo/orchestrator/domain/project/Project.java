package com.example.demo.orchestrator.domain.project;


import com.example.demo.orchestrator.persistence.test.TestSuite;

import java.time.Instant;
import java.util.*;

public class Project {

    private Long id;

    private String name;

    private String description;

    private Map<String, String> variables = new HashMap<>();

    private List<TestSuite> testSuites = new ArrayList<>();

    private final Instant createdAt;

    private Instant updatedAt;

    private Project(String name, String description) {
        validateName(name.trim());
        this.name=name.trim();
        validateDescription(description);
        this.description=description.trim();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<TestSuite> getTestSuites() {
        return Collections.unmodifiableList(testSuites);
    }

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

    public void setVariable(String name, String value){
        if (name.isBlank()) throw new IllegalArgumentException("name required");
        if (name.length() > 200) throw new IllegalArgumentException("name too long");
        if (value.isBlank()) throw new IllegalArgumentException("value required");
        if (value.length() > 2000) throw new IllegalArgumentException("value too long");
        this.variables.put(name, value);
        touch();
    }

    public boolean removeVariable(String name){
        if (!variables.containsKey(name)) throw new IllegalArgumentException("variable does not exist");
        boolean result = this.variables.remove(name) != null;
        if (result) touch();
        return result;
    }


    public void addSuite(TestSuite testSuite){
        for (TestSuite suite : testSuites) {
            if (suite.equals(testSuite)) throw new IllegalArgumentException("suite already exists");
        }
        this.testSuites.add(testSuite);
        touch();
    }

    public boolean removeSuite(TestSuite testSuite){
        boolean result = this.testSuites.remove(testSuite);
        if (result) touch();
        return result;
    }

    private Optional<TestSuite> findTestSuiteByID(Long id){
        return testSuites.stream().filter(testSuite -> testSuite.getId().equals(id)).findFirst();
    }

    private void validateName(String name) {
        if (name == null) throw new IllegalArgumentException("name must not be null");
        else if (name.isEmpty()) throw new IllegalArgumentException("name must not be blank");
        else if (name.length() > 40) throw new IllegalArgumentException("name must be at most 40 characters");
    }

    private void validateDescription(String description){
        if (description.length() > 2000) throw new IllegalArgumentException("description too long");
    }
    private void touch(){
        this.updatedAt=Instant.now();
    }
}


