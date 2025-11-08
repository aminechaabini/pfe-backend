package com.example.demo.orchestrator.domain.test.e2e;

import com.example.demo.orchestrator.domain.test.TestCase;

import java.util.List;

public class E2eTest extends TestCase {
    private List<E2eStep> steps;

    // assert value x of call 1 is equal to y of call 2
    // use value x of call 1 as input for value y of call 2

    public E2eTest(String name, String description) {
        super(name, description);
    }
}
