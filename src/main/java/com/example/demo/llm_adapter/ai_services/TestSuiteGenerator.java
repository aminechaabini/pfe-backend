package com.example.demo.llm_adapter.ai_services;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface TestSuiteGenerator {

    @SystemMessage("")
    @UserMessage("{context}")
    TestSuite generateTestSuite(String context);
}
