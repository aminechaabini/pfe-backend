package com.example.demo.orchestrator.domain.test.request;

import com.example.demo.orchestrator.domain.test.request.auth.Auth;
import com.example.demo.orchestrator.domain.test.request.body.Body;

import java.util.Map;

public class RestRequest {

    public HttpMethod method;

    public String baseUrl;

    public Map<String, String> queryParams;

    public Map<String, String> headers;

    public Auth auth;

    public String contentType;

    public Body body;


}
