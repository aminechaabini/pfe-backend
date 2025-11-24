package com.example.demo.ai.ai_services;

import com.example.demo.common.context.dto.spec2suite.suite.CreateRestTestSuiteRequest;
import com.example.demo.common.context.dto.spec2suite.suite.CreateSoapTestSuiteRequest;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface SpecToSuiteGenerator {

    @SystemMessage("""
          You are an API test generation expert. Given an API specification and test plan,
          generate a complete test suite with:

          1. Test suite name and description
          2. Individual tests with requests, assertions, and validations
          3. Proper test data and edge cases

          IMPORTANT RULES:
          - NEVER use null for any field
          - For empty headers/queryParams: use empty array []
          - For no body: use empty string "" and bodyType "NONE"
          - bodyType values: "JSON", "XML", "TEXT", "FORM", "BINARY", "NONE" (NOT "application/json")
          - For auth: set type to "" (empty string) with username="" and credential="" if no authentication needed
          - CRITICAL: ALWAYS use literal values in body field - NEVER EVER use code like repeat(), string concatenation with +, or any programming expressions
          - BAD: Using code expressions like "a".repeat(200) or string concatenation - THIS WILL CAUSE JSON VALIDATION ERRORS
          - GOOD: Using actual literal text like "aaaaaaaaaaaaaaaa..." (write out the actual characters)
          - For long test strings: skip those edge case tests or use a modest 50-80 character literal string

          ASSERTION RULES:
          - Valid assertion types: "STATUS_EQUALS", "JSONPATH_EQUALS", "JSONPATH_EXISTS", "JSON_SCHEMA_VALID", "HEADER_EQUALS", "BODY_CONTAINS", "RESPONSE_TIME_LESS_THAN", "REGEX_MATCH"
          - For STATUS_EQUALS: use target="" (empty) and expected="200" (or other status code)
          - For JSONPATH_EQUALS: use target="$.fieldName" and expected="value"
          - For HEADER_EQUALS: use target="Content-Type" and expected="application/json"
          - For BODY_CONTAINS: use target="" and expected="text to find"

          JSON STRUCTURE RULES:
          - Return a SINGLE OBJECT (not an array) with fields: name, description, variables, restApiTests
          - All tests MUST be inside the restApiTests array
          - Example structure: {"name":"Suite Name","description":"Description","variables":[],"restApiTests":[test1, test2, ...]}
          - DO NOT wrap the output in an outer array
          - DO NOT put test objects outside the restApiTests array

          Follow the approved test plan structure.
          """)
    @UserMessage("""
          Generate a REST test suite from the following:

          OpenAPI Specification:
          {{specContent}}

          Approved Test Plan (JSON):
          {{approvedPlan}}

          Generate the complete test suite following this plan with detailed requests, assertions, and test data.
          """)
    CreateRestTestSuiteRequest generateRestTestSuite(
            @V("specContent") String specContent,
            @V("approvedPlan") String approvedPlan
    );

    @SystemMessage("""
          You are a SOAP test generation expert. Given a WSDL specification and test plan,
          generate a complete test suite with:

          1. Test suite name and description
          2. Individual SOAP tests with envelopes, assertions, and validations
          3. Proper test data and edge cases

          IMPORTANT RULES:
          - NEVER use null for any field
          - For empty additionalHeaders: use empty array []
          - soapAction can be empty string "" but not null
          - soapVersion must be "1.1" or "1.2"
          - For auth: set type to "" (empty string) with username="" and credential="" if no authentication needed

          Follow the approved test plan structure.
          """)
    @UserMessage("""
          Generate a SOAP test suite from the following:

          WSDL Specification:
          {{specContent}}

          Approved Test Plan (JSON):
          {{approvedPlan}}

          Generate the complete test suite following this plan with detailed SOAP envelopes, assertions, and test data.
          """)
    CreateSoapTestSuiteRequest generateSoapTestSuite(
            @V("specContent") String specContent,
            @V("approvedPlan") String approvedPlan
    );
}
