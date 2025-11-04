package com.example.demo.orchestrator.pure_domain.project;


import com.example.demo.orchestrator.domain.test.TestSuite;
import com.example.demo.orchestrator.pure_domain.test.TestCase;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Project {

    private Long id;

    private String name;

    private String description;

    private Map<String, String> variables;

    private List<TestSuite> testSuites;

    private List<TestCase> testCases;

    private Instant createdAt;

    private Instant updatedAt;

    private Project(String name, String description) {
        this.id=null;
        setName(name);
        setDescription(description);
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

    public List<TestCase> getTestCases() {
        return Collections.unmodifiableList(testCases);
    }

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

    public void changeName(String newName){
        setName(newName);
        this.updatedAt = Instant.now();
    }

    public void changeDescription(String newDescription){
        setDescription(newDescription);
        this.updatedAt = Instant.now();
    }

    public void addVariable(String name, String value){
        if (name.isBlank()) throw new IllegalArgumentException("name required");
        if (name.length() > 200) throw new IllegalArgumentException("name too long");
        if (value.isBlank()) throw new IllegalArgumentException("value required");
        if (value.length() > 2000) throw new IllegalArgumentException("value too long");
        if (variables.containsKey(name)) throw new IllegalArgumentException("variable already exists");
        this.variables.put(name, value);
        this.updatedAt = Instant.now();
    }

    public void removeVariable(String name){
        if (!variables.containsKey(name)) throw new IllegalArgumentException("variable does not exist");
        this.variables.remove(name);
        this.updatedAt = Instant.now();
    }

    public void updateVariable(String name, String value){
        if (!variables.containsKey(name)) throw new IllegalArgumentException("variable does not exist");
        if (value.isBlank()) throw new IllegalArgumentException("value required");
        if (value.length() > 2000) throw new IllegalArgumentException("value too long");
        this.variables.put(name, value);
        this.updatedAt = Instant.now();
    }

    public void addSuite(TestSuite testSuite){
        for (TestSuite suite : testSuites) {
            if (suite.equals(testSuite)) throw new IllegalArgumentException("suite already exists");
        }
        this.testSuites.add(testSuite);
        this.updatedAt = Instant.now();
    }

    public void removeSuite(TestSuite testSuite){
        if (!testSuites.contains(testSuite)) throw new IllegalArgumentException("suite does not exist");
        this.testSuites.remove(testSuite);
        this.updatedAt = Instant.now();
    }

    public void addTestCase(TestCase testCase){
        for (TestCase tc : testCases) {
            if (tc.equals(testCase)) throw new IllegalArgumentException("test case already exists");
        }
        this.testCases.add(testCase);
        this.updatedAt = Instant.now();
    }

    public void removeTestCase(TestCase testCase){
        if (!testCases.contains(testCase)) throw new IllegalArgumentException("test case does not exist");
        this.testCases.remove(testCase);
        this.updatedAt = Instant.now();
    }
}


