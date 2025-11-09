package com.example.demo.orchestrator.domain.run;

import com.example.demo.orchestrator.domain.test.test_suite.TestSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TestSuiteRun extends Run {
    
    private TestSuite testSuite;
    private final List<TestCaseRun> testCaseRuns = new ArrayList<>();

    public TestSuiteRun() {
        super();
    }

    public TestSuite getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(TestSuite testSuite) {
        this.testSuite = Objects.requireNonNull(testSuite, "Test suite cannot be null");
    }

    /**
     * Add a test case run to this suite run.
     */
    public void addTestCaseRun(TestCaseRun testCaseRun) {
        Objects.requireNonNull(testCaseRun, "Test case run cannot be null");
        this.testCaseRuns.add(testCaseRun);
    }

    /**
     * Get all test case runs (unmodifiable view).
     */
    public List<TestCaseRun> getTestCaseRuns() {
        return Collections.unmodifiableList(testCaseRuns);
    }

    /**
     * Check if all test cases in the suite passed.
     */
    public boolean allTestCasesPassed() {
        return !testCaseRuns.isEmpty() && 
               testCaseRuns.stream().allMatch(Run::isSuccessful);
    }

    /**
     * Get the number of passed test cases.
     */
    public long getPassedTestCasesCount() {
        return testCaseRuns.stream().filter(Run::isSuccessful).count();
    }

    /**
     * Get the number of failed test cases.
     */
    public long getFailedTestCasesCount() {
        return testCaseRuns.stream().filter(Run::isFailed).count();
    }
}
