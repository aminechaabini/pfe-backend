package com.example.demo.orchestrator.domain.test.api;

import com.example.demo.orchestrator.domain.test.TestCase;
import com.example.demo.orchestrator.domain.test.assertion.Assertion;
import com.example.demo.orchestrator.domain.test.request.HttpRequest;
import com.example.demo.orchestrator.domain.test.request.body.Body;

import java.util.List;

public abstract class ApiTest extends TestCase {

    private HttpRequest<Body> request;

    private List<Assertion> assertions;

    public ApiTest(String name, String description) {
        super(name, description);
    }

}
