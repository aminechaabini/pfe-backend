package com.example.demo.orchestrator.domain.run;

import com.example.demo.orchestrator.domain.test.test_suite.TestSuite;

import java.util.List;

public class TestSuiteRun extends Run{
    private TestSuite testSuite;
    private List<TestCaseRun> testCaseRuns;
}
