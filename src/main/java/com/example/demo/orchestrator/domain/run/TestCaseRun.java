package com.example.demo.orchestrator.domain.run;

import com.example.demo.orchestrator.domain.test.TestCase;

import java.util.Objects;

public abstract class TestCaseRun extends Run {
    
    private TestCase testCase;

    protected TestCaseRun() {
        super();
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = Objects.requireNonNull(testCase, "Test case cannot be null");
    }
}
