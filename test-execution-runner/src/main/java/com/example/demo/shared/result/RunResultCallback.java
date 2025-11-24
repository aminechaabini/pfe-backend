package com.example.demo.shared.result;

/**
 * Callback interface for asynchronous run result notifications.
 */
@FunctionalInterface
public interface RunResultCallback {
    void onComplete(RunResult result);
}
