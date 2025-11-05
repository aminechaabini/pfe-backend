package com.example.demo.orchestrator.domain.test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class TestSuite extends Runnable {
    private Map<String, String> variables;
    private List<TestCase> testCases;

    public TestSuite(Long id, String name, String description, String type) {
        super(name, description, type);
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

    public void addTestCase(TestCase testCase){
        for (TestCase tc : testCases) {
            if (tc.equals(testCase)) throw new IllegalArgumentException("test case already exists");
        }
        this.testCases.add(testCase);
        this.updatedAt = Instant.now();
    }

    public boolean removeTestCase(TestCase testCase){
        boolean result = this.testCases.remove(testCase);
        if (result) touch();
        return result;
    }

}
