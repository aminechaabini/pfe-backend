package com.example.demo.orchestrator.domain.test.api;

import com.example.demo.orchestrator.domain.test.assertion.Assertion;
import com.example.demo.orchestrator.domain.test.request.RestRequest;


import java.util.List;

public class RestApiTest extends ApiTest{


    public RestApiTest(String name, String description, String type, RestRequest request, List<Assertion> assertions) {
        super(name, description, type);
        this.request = request;
        this.assertions = assertions;
    }

    private RestRequest request;

    private List<Assertion> assertions;
}
