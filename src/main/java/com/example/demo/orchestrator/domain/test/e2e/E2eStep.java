package com.example.demo.orchestrator.domain.test.e2e;

import com.example.demo.orchestrator.domain.test.assertion.Assertion;
import com.example.demo.orchestrator.domain.test.request.HttpRequest;
import com.example.demo.orchestrator.domain.test.request.body.Body;

import java.util.List;

public class E2eStep {

    private Integer orderIndex;

    private HttpRequest<Body> request;

    private List<Assertion> assertions;

    private List<ExtractorItem> extractorItems;

}
