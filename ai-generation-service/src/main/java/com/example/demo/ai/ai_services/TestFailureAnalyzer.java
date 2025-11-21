package com.example.demo.ai.ai_services;

import com.example.demo.shared.context.dto.analysis.FailureAnalysis;
import com.example.demo.shared.context.RestFailureAnalysisContext;
import com.example.demo.shared.context.SoapFailureAnalysisContext;
import com.example.demo.shared.context.E2eFailureAnalysisContext;
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

          Test Name: {{context.testName}}
          Endpoint: {{context.method}} {{context.url}}

          Request Body:
          {{context.requestBody}}

          Expected Status: {{context.expectedStatus}}
          Actual Status: {{context.actualStatus}}

          Actual Response Body:
          {{context.actualBody}}

          Failed Assertion: {{context.failedAssertion}}

          Was passing before: {{context.wasPassingBefore}}

          Explain why this test failed and how to fix it.
          """)
    FailureAnalysis analyzeRestFailure(@V("context") RestFailureAnalysisContext context);

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

          Test Name: {{context.testName}}
          Endpoint: {{context.url}}
          SOAPAction: {{context.soapAction}}

          SOAP Request Envelope:
          {{context.soapEnvelope}}

          Expected Status: {{context.expectedStatus}}
          Actual Status: {{context.actualStatus}}

          Actual Response:
          {{context.actualBody}}

          Failed Assertion: {{context.failedAssertion}}

          Was passing before: {{context.wasPassingBefore}}

          Explain why this test failed and how to fix it.
          """)
    FailureAnalysis analyzeSoapFailure(@V("context") SoapFailureAnalysisContext context);

    @SystemMessage("""
          You are an E2E workflow test debugging expert. Analyze test failures and provide:
          1. Root cause in plain English
          2. Specific fix recommendations
          3. Whether it's a test issue, API issue, or environment issue

          Be concise and actionable. Consider data flow between steps and variable extraction.
          """)
    @UserMessage("""
          E2E test failed with the following details:

          Test Name: {{context.testName}}
          Failed at Step: {{context.failedStepIndex}} - {{context.failedStepName}}
          Step Type: {{context.stepType}}
          Endpoint: {{context.method}} {{context.url}}

          Request:
          {{context.request}}

          Expected Status: {{context.expectedStatus}}
          Actual Status: {{context.actualStatus}}

          Actual Response:
          {{context.actualBody}}

          Failed Assertion: {{context.failedAssertion}}

          Variables extracted from previous steps:
          {{context.extractedVariables}}

          Was passing before: {{context.wasPassingBefore}}

          Explain why this test failed and how to fix it.
          """)
    FailureAnalysis analyzeE2eFailure(@V("context") E2eFailureAnalysisContext context);
}