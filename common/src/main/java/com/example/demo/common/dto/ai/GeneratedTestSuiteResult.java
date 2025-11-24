package com.example.demo.common.dto.ai;

import com.example.demo.common.context.dto.spec2suite.suite.CreateRestTestSuiteRequest;
import com.example.demo.common.context.dto.spec2suite.suite.CreateSoapTestSuiteRequest;

/**
 * Result wrapper for generated test suites from AI.
 * Can contain either REST or SOAP test suite.
 */
public record GeneratedTestSuiteResult(
    CreateRestTestSuiteRequest restTestSuite,
    CreateSoapTestSuiteRequest soapTestSuite
) {
    public GeneratedTestSuiteResult {
        if (restTestSuite == null && soapTestSuite == null) {
            throw new IllegalArgumentException("Either REST or SOAP test suite must be provided");
        }
        if (restTestSuite != null && soapTestSuite != null) {
            throw new IllegalArgumentException("Cannot have both REST and SOAP test suite");
        }
    }

    public static GeneratedTestSuiteResult forRest(CreateRestTestSuiteRequest suite) {
        return new GeneratedTestSuiteResult(suite, null);
    }

    public static GeneratedTestSuiteResult forSoap(CreateSoapTestSuiteRequest suite) {
        return new GeneratedTestSuiteResult(null, suite);
    }

    public boolean isRest() {
        return restTestSuite != null;
    }

    public boolean isSoap() {
        return soapTestSuite != null;
    }
}
