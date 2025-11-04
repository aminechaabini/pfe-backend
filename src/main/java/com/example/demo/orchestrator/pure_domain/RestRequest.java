package com.example.demo.orchestrator.pure_domain;

import java.util.Map;

public class RestRequest extends Request{

    public String method;

    public String url;
    public Map<String, String> queryParams;

    public Map<String, String> headers;
    public Map<String, String> auth;

    public String body;

    public String Assertions;

    public String OtherTestsBesidesAssertions;


}
