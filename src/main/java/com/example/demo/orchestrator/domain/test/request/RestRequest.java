package com.example.demo.orchestrator.domain.test.request;

import com.example.demo.orchestrator.domain.test.request.auth.Auth;
import com.example.demo.orchestrator.domain.test.request.body.Body;

import java.util.Map;

public class RestRequest extends HttpRequest<Body> {

    private Map<String, String> queryParams;

    private Auth auth;

    private String contentType;




}
