package com.example.demo.common.ports;

import com.example.demo.shared.request.RunRequest;
import com.example.demo.shared.result.RunResult;
import com.example.demo.shared.result.RunResultCallback;

/**
 * Port for test execution service.
 * Defines the contract for executing tests asynchronously.
 *
 * <p>This is the primary interface for the test-execution-service module.
 * Clients should depend only on this interface, not on implementation details.
 *
 * <p>Usage:
 * <pre>
 * testExecutionPort.submit(runRequest, result -> {
 *     System.out.println("Test completed: " + result.status());
 * });
 * </pre>
 */
public interface TestExecutionPort {

    /**
     * Submit a test run request for asynchronous execution.
     *
     * @param request the run request (REST, SOAP, or E2E)
     * @param callback invoked when execution completes
     */
    void submit(RunRequest request, RunResultCallback callback);

    /**
     * Get the current number of pending runs in the queue.
     *
     * @return number of pending runs
     */
    int getQueueSize();

    /**
     * Shutdown the test execution service gracefully.
     * Waits for current run to complete but clears pending queue.
     */
    void shutdown();
}
