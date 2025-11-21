package com.example.demo.runner.builder;


import com.example.demo.core.domain.test.request.HttpRequest;

import java.util.Map;

public interface RequestBuilder {
    java.net.http.HttpRequest build(HttpRequest request, Map<String, String> variables);
}
