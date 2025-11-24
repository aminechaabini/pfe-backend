package com.example.demo.ai.config;

import dev.langchain4j.model.openai.OpenAiChatModelName;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for OpenAI integration.
 * Prefix: openai
 */
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    /**
     * OpenAI API key (required).
     * Get yours from: https://platform.openai.com/api-keys
     */
    private String apiKey;

    /**
     * Base URL for OpenAI API (default: https://api.openai.com/v1).
     * Change this if you are using a different OpenAI instance.
     */
    private String baseUrl;


    /**
     * Model name to use (default: gpt-4o).
     * Options: gpt-4o, gpt-4o-mini, gpt-4-turbo, gpt-3.5-turbo, etc.
     */
    private String modelName;

    /**
     * Temperature for response generation (0.0 - 2.0).
     * Lower = more deterministic, Higher = more creative.
     * Default: 0.7
     */
    private double temperature = 0.7;

    /**
     * Maximum tokens in the response.
     * Default: 4096
     */
    private int maxTokens = 4096;

    /**
     * Timeout in seconds for API calls.
     * Default: 60
     */
    private long timeoutSeconds = 60;

    /**
     * Log requests to OpenAI (for debugging).
     * Default: false
     */
    private boolean logRequests = false;

    /**
     * Log responses from OpenAI (for debugging).
     * Default: false
     */
    private boolean logResponses = false;

    // Getters and Setters

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }


    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(long timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public boolean isLogRequests() {
        return logRequests;
    }

    public void setLogRequests(boolean logRequests) {
        this.logRequests = logRequests;
    }

    public boolean isLogResponses() {
        return logResponses;
    }

    public void setLogResponses(boolean logResponses) {
        this.logResponses = logResponses;
    }
}
