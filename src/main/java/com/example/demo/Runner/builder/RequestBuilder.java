package com.example.demo.Runner.builder;


import com.example.demo.orchestrator.domain.test.request.HttpRequest;

import java.util.Map;

public interface RequestBuilder {
    java.net.http.HttpRequest build(HttpRequest request, Map<String, String> variables);
}
