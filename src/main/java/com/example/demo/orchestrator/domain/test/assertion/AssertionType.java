package com.example.demo.orchestrator.domain.test.assertion;

public enum AssertionType {
    STATUS_EQUALS,
    HEADER_EQUALS,
    BODY_CONTAINS,
    JSONPATH_EQUALS,
    JSONPATH_EXISTS,
    JSON_SCHEMA_VALID,
    XPATH_EQUALS,
    XPATH_EXISTS,
    XSD_VALID,
    RESPONSE_TIME_LESS_THAN,
    REGEX_MATCH,
}
