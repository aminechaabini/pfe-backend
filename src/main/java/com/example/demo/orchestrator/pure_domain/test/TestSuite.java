package com.example.demo.orchestrator.pure_domain.test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class TestSuite extends Runnable {
    private Map<String, String> variables;
    private List<TestCase> testCases;

    public TestSuite(Long id, String name, String description, RunnableType type, Map<String, String> variables, List<TestCase> testCases) {
        super(id, name, description, type);
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
