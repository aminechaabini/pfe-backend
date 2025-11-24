package com.example.demo.ai.ai_services;

import com.example.demo.common.context.dto.analysis.FailureAnalysis;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface TestFailureAnalyzer {

    @SystemMessage("""
          You are a REST API test debugging expert. Analyze test failures and provide:
          1. Root cause in plain English
          2. Specific fix recommendations
          3. Whether it's a test issue, API issue, or environment issue

          Be concise and actionable.
          """)
    @UserMessage("""
          REST test failed with the following details:

          Test Name: {{testName}}
          Endpoint: {{method}} {{url}}

          Request Body:
          {{requestBody}}

          Expected Status: {{expectedStatus}}
          Actual Status: {{actualStatus}}

          Actual Response Body:
          {{actualBody}}

          Failed Assertion: {{failedAssertion}}

          Was passing before: {{wasPassingBefore}}

          Explain why this test failed and how to fix it.
          """)
    FailureAnalysis analyzeRestFailure(
            @V("testName") String testName,
            @V("method") String method,
            @V("url") String url,
            @V("requestBody") String requestBody,
            @V("expectedStatus") String expectedStatus,
            @V("actualStatus") String actualStatus,
            @V("actualBody") String actualBody,
            @V("failedAssertion") String failedAssertion,
            @V("wasPassingBefore") String wasPassingBefore
    );

    @SystemMessage("""
          You are a SOAP API test debugging expert. Analyze test failures and provide:
          1. Root cause in plain English
          2. Specific fix recommendations
          3. Whether it's a test issue, API issue, or environment issue

          Be concise and actionable. Consider SOAP-specific issues like envelope structure,
          namespaces, SOAP faults, and SOAPAction headers.
          """)
    @UserMessage("""
          SOAP test failed with the following details:

          Test Name: {{testName}}
          Endpoint: {{url}}
          SOAPAction: {{soapAction}}

          SOAP Request Envelope:
          {{soapEnvelope}}

          Expected Status: {{expectedStatus}}
          Actual Status: {{actualStatus}}

          Actual Response:
          {{actualBody}}

          Failed Assertion: {{failedAssertion}}

          Was passing before: {{wasPassingBefore}}

          Explain why this test failed and how to fix it.
          """)
    FailureAnalysis analyzeSoapFailure(
            @V("testName") String testName,
            @V("url") String url,
            @V("soapEnvelope") String soapEnvelope,
            @V("soapAction") String soapAction,
            @V("expectedStatus") String expectedStatus,
            @V("actualStatus") String actualStatus,
            @V("actualBody") String actualBody,
            @V("failedAssertion") String failedAssertion,
            @V("wasPassingBefore") String wasPassingBefore
    );

    @SystemMessage("""
          You are an E2E workflow test debugging expert. Analyze test failures and provide:
          1. Root cause in plain English
          2. Specific fix recommendations
          3. Whether it's a test issue, API issue, or environment issue

          Be concise and actionable. Consider data flow between steps and variable extraction.
          """)
    @UserMessage("""
          E2E test failed with the following details:

          Test Name: {{testName}}
          Failed at Step: {{failedStepIndex}} - {{failedStepName}}
          Step Type: {{stepType}}
          Endpoint: {{method}} {{url}}

          Request:
          {{request}}

          Expected Status: {{expectedStatus}}
          Actual Status: {{actualStatus}}

          Actual Response:
          {{actualBody}}

          Failed Assertion: {{failedAssertion}}

          Variables extracted from previous steps:
          {{extractedVariables}}

          Was passing before: {{wasPassingBefore}}

          Explain why this test failed and how to fix it.
          """)
    FailureAnalysis analyzeE2eFailure(
            @V("testName") String testName,
            @V("failedStepIndex") String failedStepIndex,
            @V("failedStepName") String failedStepName,
            @V("stepType") String stepType,
            @V("method") String method,
            @V("url") String url,
            @V("request") String request,
            @V("expectedStatus") String expectedStatus,
            @V("actualStatus") String actualStatus,
            @V("actualBody") String actualBody,
            @V("failedAssertion") String failedAssertion,
            @V("extractedVariables") String extractedVariables,
            @V("wasPassingBefore") String wasPassingBefore
    );
}
