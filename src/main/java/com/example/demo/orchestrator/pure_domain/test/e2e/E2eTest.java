package com.example.demo.orchestrator.pure_domain.test.e2e;

import com.example.demo.orchestrator.pure_domain.test.RunnableType;
import com.example.demo.orchestrator.pure_domain.test.TestCase;

import java.util.List;

public class E2eTest extends TestCase {
    private List<E2eStep> steps;

    // assert value x of call 1 is equal to y of call 2
    // use value x of call 1 as input for value y of call 2

    public E2eTest(Long id, String name, String description, RunnableType type) {
        super(id, name, description, type);
    }
}
