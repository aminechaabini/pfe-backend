package com.example.demo.ai.ai_services;

import com.example.demo.common.context.dto.analysis.RestSpecUpdateAnalysis;
import com.example.demo.common.context.dto.analysis.SoapSpecUpdateAnalysis;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface SpecUpdateAnalyzer {

    @SystemMessage("""
          You are a REST API change analysis expert. Compare old and new OpenAPI specifications
          and identify:

          1. Breaking changes that will affect existing tests
          2. New endpoints added
          3. Removed endpoints
          4. Schema changes that require test updates

          For each affected test, provide:
          - Why it's affected
          - Suggested action: "regenerate" (use spec2suite), "delete" (endpoint removed), or "manual_review"

          Provide actionable recommendations.
          """)
    @UserMessage("""
          Analyze changes between OpenAPI specifications:

          OLD SPEC:
          {{oldSpecContent}}

          NEW SPEC:
          {{newSpecContent}}

          EXISTING TESTS (JSON):
          {{existingTests}}

          Compare the specs and analyze impact on existing tests.
          """)
    RestSpecUpdateAnalysis analyzeRestSpecUpdate(
            @V("oldSpecContent") String oldSpecContent,
            @V("newSpecContent") String newSpecContent,
            @V("existingTests") String existingTests
    );

    @SystemMessage("""
          You are a SOAP API change analysis expert. Compare old and new WSDL specifications
          and identify:

          1. Breaking changes that will affect existing tests
          2. New operations added
          3. Removed operations
          4. Schema/namespace changes that require test updates

          For each affected test, provide:
          - Why it's affected
          - Suggested action: "regenerate" (use spec2suite), "delete" (operation removed), or "manual_review"

          Provide actionable recommendations.
          """)
    @UserMessage("""
          Analyze changes between WSDL specifications:

          OLD SPEC:
          {{oldSpecContent}}

          NEW SPEC:
          {{newSpecContent}}

          EXISTING TESTS (JSON):
          {{existingTests}}

          Compare the specs and analyze impact on existing tests.
          """)
    SoapSpecUpdateAnalysis analyzeSoapSpecUpdate(
            @V("oldSpecContent") String oldSpecContent,
            @V("newSpecContent") String newSpecContent,
            @V("existingTests") String existingTests
    );
}
