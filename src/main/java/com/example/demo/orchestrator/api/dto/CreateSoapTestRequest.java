package com.example.demo.orchestrator.api.dto;

import com.example.demo.orchestrator.domain.test.assertion.Assertion;
import com.example.demo.orchestrator.domain.test.request.SoapRequest;

import java.util.List;

/**
 * Request DTO for creating a SOAP API test.
 */
public record CreateSoapTestRequest(
        String name,
        String description,
        SoapRequest request,
        List<Assertion> assertions
) {}
