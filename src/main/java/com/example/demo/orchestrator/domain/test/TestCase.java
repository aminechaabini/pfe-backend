package com.example.demo.orchestrator.domain.test;

public abstract class TestCase extends Runnable {

    public TestCase(Long id, String name, String description, String type) {
        super(name, description, type);
    }
}
