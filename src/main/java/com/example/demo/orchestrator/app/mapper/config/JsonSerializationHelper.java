package com.example.demo.orchestrator.app.mapper.config;

import com.example.demo.orchestrator.domain.test.assertion.Assertion;
import com.example.demo.orchestrator.domain.test.request.HttpRequest;
import com.example.demo.orchestrator.domain.test.request.RestRequest;
import com.example.demo.orchestrator.domain.test.request.SoapRequest;
import com.example.demo.orchestrator.domain.test.request.auth.Auth;
import com.example.demo.orchestrator.domain.test.request.body.Body;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Helper class for serializing and deserializing complex domain objects to/from JSON.
 *
 * This is used by mappers to convert domain objects (HttpRequest, Body, Assertion, Auth)
 * into JSON strings for storage in persistence entities.
 *
 * Uses Jackson ObjectMapper with polymorphic type handling for proper deserialization
 * of abstract types (Body, Auth, HttpRequest).
 */
@Component
public class JsonSerializationHelper {

    private final ObjectMapper objectMapper;

    public JsonSerializationHelper() {
        this.objectMapper = new ObjectMapper();

        // Enable polymorphic type handling for abstract classes/interfaces
        BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.example.demo.orchestrator.domain")
                .build();

        objectMapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);
    }

    // ==================== HttpRequest Serialization ====================

    /**
     * Serialize an HttpRequest to JSON string.
     * Handles both RestRequest and SoapRequest polymorphically.
     */
    public String serializeHttpRequest(HttpRequest<?> request) {
        if (request == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize HttpRequest to JSON", e);
        }
    }

    /**
     * Deserialize a JSON string to HttpRequest.
     * Returns appropriate subtype (RestRequest or SoapRequest).
     */
    public HttpRequest<?> deserializeHttpRequest(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, HttpRequest.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize JSON to HttpRequest", e);
        }
    }

    // ==================== Body Serialization ====================

    /**
     * Serialize a Body to JSON string.
     * Handles all body types polymorphically (NoBody, JsonBody, XmlBody, etc.).
     */
    public String serializeBody(Body body) {
        if (body == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize Body to JSON", e);
        }
    }

    /**
     * Deserialize a JSON string to Body.
     * Returns appropriate subtype based on polymorphic type information.
     */
    public Body deserializeBody(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Body.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize JSON to Body", e);
        }
    }

    // ==================== Assertion Serialization ====================

    /**
     * Serialize a single Assertion to JSON string.
     */
    public String serializeAssertion(Assertion assertion) {
        if (assertion == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(assertion);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize Assertion to JSON", e);
        }
    }

    /**
     * Deserialize a JSON string to Assertion.
     */
    public Assertion deserializeAssertion(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Assertion.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize JSON to Assertion", e);
        }
    }

    /**
     * Serialize a list of Assertions to JSON string.
     */
    public String serializeAssertions(List<Assertion> assertions) {
        if (assertions == null || assertions.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(assertions);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize Assertions list to JSON", e);
        }
    }

    /**
     * Deserialize a JSON string to list of Assertions.
     */
    public List<Assertion> deserializeAssertions(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Assertion>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize JSON to Assertions list", e);
        }
    }

    // ==================== Auth Serialization ====================

    /**
     * Serialize an Auth to JSON string.
     * Handles all auth types polymorphically (BasicAuth, BearerTokenAuth).
     */
    public String serializeAuth(Auth auth) {
        if (auth == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(auth);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize Auth to JSON", e);
        }
    }

    /**
     * Deserialize a JSON string to Auth.
     * Returns appropriate subtype based on polymorphic type information.
     */
    public Auth deserializeAuth(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Auth.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize JSON to Auth", e);
        }
    }

    // ==================== ExtractorItem Serialization ====================

    /**
     * Serialize a list of ExtractorItems to JSON string.
     */
    public String serializeExtractors(List<com.example.demo.orchestrator.domain.test.e2e.ExtractorItem> extractors) {
        if (extractors == null || extractors.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(extractors);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize ExtractorItems list to JSON", e);
        }
    }

    /**
     * Deserialize a JSON string to list of ExtractorItems.
     */
    public List<com.example.demo.orchestrator.domain.test.e2e.ExtractorItem> deserializeExtractors(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<com.example.demo.orchestrator.domain.test.e2e.ExtractorItem>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize JSON to ExtractorItems list", e);
        }
    }
}
