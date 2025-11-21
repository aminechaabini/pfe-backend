package com.example.demo.core.application.ports;

import com.example.demo.core.domain.run.TestCaseRun;
import com.example.demo.core.domain.run.TestSuiteRun;
import com.example.demo.core.domain.test.TestCase;
import com.example.demo.core.domain.test.test_suite.TestSuite;

import java.util.Map;

/**
 * Port interface for test execution.
 *
 * This interface defines the contract for executing API tests.
 * The core module depends on this abstraction, not on the concrete implementation.
 */
public interface TestExecutionPort {

    /**
     * Execute a single test case.
     *
     * @param testCase The test case to execute
     * @param variables Resolved variables (project + suite + environment)
     * @return Test case run result with status, response, assertions
     */
    TestCaseRun executeTestCase(TestCase testCase, Map<String, String> variables);

    /**
     * Execute a test suite (multiple test cases).
     *
     * @param testSuite The test suite to execute
     * @param variables Resolved variables (project + suite + environment)
     * @return Test suite run result with all test case runs
     */
    TestSuiteRun executeTestSuite(TestSuite testSuite, Map<String, String> variables);

    /**
     * Check if the runner service is healthy and ready to execute tests.
     *
     * @return true if the service is healthy
     */
    boolean isHealthy();
}
