package com.example.demo.llm_adapter.ai_services;

import com.example.demo.llm_adapter.dto.spec2suite.suite.CreateRestTestSuiteRequest;
import com.example.demo.llm_adapter.dto.spec2suite.suite.CreateSoapTestSuiteRequest;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface SpecToSuiteGenerator {

    @SystemMessage("""
          You are an API test generation expert. Given an API specification and test plan,
          generate a complete test suite with:

          1. Test suite name and description
          2. Individual tests with requests, assertions, and validations
          3. Proper test data and edge cases

          Follow the approved test plan structure.
          """)
    @UserMessage("{context}")
    CreateRestTestSuiteRequest generateRestTestSuite(String context);

    @SystemMessage("""
          You are a SOAP test generation expert. Given a WSDL specification and test plan,
          generate a complete test suite with:

          1. Test suite name and description
          2. Individual SOAP tests with envelopes, assertions, and validations
          3. Proper test data and edge cases

          Follow the approved test plan structure.
          """)
    @UserMessage("{context}")
    CreateSoapTestSuiteRequest generateSoapTestSuite(String context);
}
