package com.example.demo.Runner.ledger;

public interface RunLedger {
    boolean isActiveOrDone(String runId, String idempotencyKey); // true => short-circuit
    void markDispatched(String runId, String idempotencyKey);
}