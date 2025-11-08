package com.example.demo.orchestrator.domain.run;


import com.example.demo.orchestrator.domain.test.assertion.Assertion;

public record AssertionResult(Assertion assertion, boolean ok, String message) {
}

