package com.example.demo.orchestrator.pure_domain.test.e2e;

import com.example.demo.orchestrator.pure_domain.Request;
import com.example.demo.shared.events.AssertionSpec;

import java.util.List;

public class E2eStep {

    private Integer orderIndex;

    private Request request;

    private List<AssertionSpec> assertions;

    private List<ExtractorItem> extractorItems;

}
