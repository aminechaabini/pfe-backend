package com.example.demo.ai.config;

import com.example.demo.ai.ai_services.*;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Set;

import static dev.langchain4j.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;

/**
 * Configuration for LangChain4j AI services.
 * Creates the OpenAI chat model and all AI service proxies.
 */
@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
public class LangChain4jConfig {

    /**
     * Create the OpenAI chat model bean.
     * This is the underlying LLM used by all AI services.
     */
    @Bean
    public ChatModel chatLanguageModel(OpenAiProperties properties) {
        return  OpenAiChatModel.builder()
            .apiKey("")
            .baseUrl("https://router.huggingface.co/v1")
            .modelName("openai" +
                    "/" +
                    "gpt-oss-20b")
            .strictJsonSchema(true) // Force strict adherence to JSON schema
            .supportedCapabilities(Set.of(RESPONSE_FORMAT_JSON_SCHEMA)) // Declare support for JSON schema
            .build();
    }
//
//    OpenAiChatModel chatModel = OpenAiChatModel.builder()
//            .apiKey("")
//            .baseUrl("https://router.huggingface.co/v1")
//            .modelName("meta-llama/Llama-3.1-8B-Instruct")
//            .strictJsonSchema(true) // Force strict adherence to JSON schema
//            .supportedCapabilities(Set.of(RESPONSE_FORMAT_JSON_SCHEMA)) // Declare support for JSON schema
//            .build();

    /**
     * Test generation planner - creates test plans for user approval.
     */
    @Bean
    public TestGenerationPlanner testGenerationPlanner(ChatModel chatModel) {
        return AiServices.builder(TestGenerationPlanner.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * Spec to suite generator - generates complete test suites from specs.
     */
    @Bean
    public SpecToSuiteGenerator specToSuiteGenerator(ChatModel chatModel) {
        return AiServices.builder(SpecToSuiteGenerator.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * E2E workflow generator - creates end-to-end test workflows.
     */
    @Bean
    public E2eWorkflowGenerator e2eWorkflowGenerator(ChatModel chatModel) {
        return AiServices.builder(E2eWorkflowGenerator.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * Test failure analyzer - analyzes test failures and suggests fixes.
     */
    @Bean
    public TestFailureAnalyzer testFailureAnalyzer(ChatModel chatModel) {
        return AiServices.builder(TestFailureAnalyzer.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * Spec update analyzer - analyzes API spec changes and impact on tests.
     */
    @Bean
    public SpecUpdateAnalyzer specUpdateAnalyzer(ChatModel chatModel) {
        return AiServices.builder(SpecUpdateAnalyzer.class)
                .chatModel(chatModel)
                .build();
    }
}
