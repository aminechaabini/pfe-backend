package com.example.demo.core.domain.run;


import com.example.demo.core.domain.test.assertion.Assertion;

public record AssertionResult(Assertion assertion, boolean ok, String message) {
}

