package com.example.demo.core.domain.test.assertion;

import com.example.demo.core.domain.test.request.body.Body;
import com.example.demo.core.domain.test.request.body.JsonBody;
import com.example.demo.core.domain.test.request.body.XmlBody;

/**
 * Types of assertions that can be performed on API responses.
 * Each type is categorized by what it requires to function.
 */
public enum AssertionType {
    // General assertions - work with any response
    STATUS_EQUALS(Category.GENERAL),
    HEADER_EQUALS(Category.GENERAL),
    BODY_CONTAINS(Category.GENERAL),
    RESPONSE_TIME_LESS_THAN(Category.GENERAL),
    REGEX_MATCH(Category.GENERAL),

    // JSON-specific assertions - require JSON response body
    JSONPATH_EQUALS(Category.JSON),
    JSONPATH_EXISTS(Category.JSON),
    JSON_SCHEMA_VALID(Category.JSON),

    // XML-specific assertions - require XML response body
    XPATH_EQUALS(Category.XML),
    XPATH_EXISTS(Category.XML),
    XSD_VALID(Category.XML);

    private final Category category;

    AssertionType(Category category) {
        this.category = category;
    }

    public boolean requiresJson() {
        return category == Category.JSON;
    }

    public boolean requiresXml() {
        return category == Category.XML;
    }

    public boolean isCompatibleWith(Body body) {
        return switch (category) {
            case GENERAL -> true;
            case JSON -> body instanceof JsonBody;
            case XML -> body instanceof XmlBody;
        };
    }

    public enum Category {
        GENERAL, JSON, XML
    }
}
