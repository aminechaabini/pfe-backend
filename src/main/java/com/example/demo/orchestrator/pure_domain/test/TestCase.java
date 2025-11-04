package com.example.demo.orchestrator.pure_domain.test;

public abstract class TestCase extends Runnable {

    public TestCase(Long id, String name, String description, RunnableType type) {
        super(id, name, description, type);
    }
}
