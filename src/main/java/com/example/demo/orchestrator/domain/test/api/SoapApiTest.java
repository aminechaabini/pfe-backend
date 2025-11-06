package com.example.demo.orchestrator.domain.test.api;

import com.example.demo.orchestrator.domain.test.assertion.Assertion;
import com.example.demo.orchestrator.domain.test.request.SoapRequest;

import java.util.List;

public class SoapApiTest extends ApiTest
{
    public SoapApiTest(String name, String description, String type, SoapRequest request, List<Assertion> assertions) {
        super(name, description, type);
        this.request = request;
        this.assertions = assertions;
    }

    private SoapRequest request;

    List<Assertion> assertions;
}
