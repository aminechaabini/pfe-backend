package com.example.demo.llm_adapter.ai_services;


import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface TestsPreviewGenerator {
    @SystemMessage("You are a test preview generator. You will be given a context and you will generate test previews based on it.")
    @UserMessage("{context}")
    List<TestPreview> generatePreviews(String context);
}
