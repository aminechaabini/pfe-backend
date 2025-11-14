package com.example.demo.Runner;

import com.example.demo.shared.result.RunResult;

/**
 * Callback for handling run results asynchronously.
 * Invoked by the Runner when a test execution completes.
 */
@FunctionalInterface
public interface RunResultCallback {

    /**
     * Called when a run completes (success or failure).
     *
     * @param result the execution result
     */
    void onComplete(RunResult result);
}
