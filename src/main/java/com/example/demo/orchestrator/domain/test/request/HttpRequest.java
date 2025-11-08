package com.example.demo.orchestrator.domain.test.request;

import com.example.demo.orchestrator.domain.test.request.body.Body;

import java.util.List;
import java.util.Map;

public class HttpRequest <B extends Body>{

    private HttpMethod method;

    private String url;

    private Map<String, List<String>> headers;

    private B body;


}
