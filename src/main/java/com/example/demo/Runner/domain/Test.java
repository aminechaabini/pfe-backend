package com.example.demo.Runner.domain;

import java.util.List;
import java.util.Map;

public record Test(
        String protocol,           // "REST" (or "SOAP" later)
        String operation,          // e.g., "GET", "POST"
        String target,             // full URL or endpoint
        Map<String,String> headers,
        byte[] body,               // optional
        List<String> assertions // status == 200, jsonpath("$.id") exists, etc.
) {}