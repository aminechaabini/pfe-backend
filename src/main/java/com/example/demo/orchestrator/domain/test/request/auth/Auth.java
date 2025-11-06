package com.example.demo.orchestrator.domain.test.request.auth;

import java.util.List;
import java.util.Map;

public interface Auth {

    public void applyTo(Map<String, List<String>> headers);
}
