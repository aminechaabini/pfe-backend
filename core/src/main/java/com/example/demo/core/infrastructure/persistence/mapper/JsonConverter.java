package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.test.e2e.ExtractorItem;
import com.example.demo.core.domain.test.request.HttpRequest;
import com.example.demo.core.domain.test.request.RestRequest;
import com.example.demo.core.domain.test.request.SoapRequest;
import com.example.demo.core.domain.test.request.body.Body;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Centralized JSON converter for domain objects.
 * Handles serialization/deserialization between domain objects and JSON strings.
 */
@Component
public class JsonConverter {

    private final ObjectMapper objectMapper;

    public JsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // ===============================
    // REST REQUEST CONVERSION
    // ===============================

    public RestRequest jsonToRestRequest(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, RestRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize RestRequest from JSON", e);
        }
    }

    public String restRequestToJson(RestRequest request) {
        if (request == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize RestRequest to JSON", e);
        }
    }

    // ===============================
    // SOAP REQUEST CONVERSION
    // ===============================

    public SoapRequest jsonToSoapRequest(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, SoapRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize SoapRequest from JSON", e);
        }
    }

    public String soapRequestToJson(SoapRequest request) {
        if (request == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize SoapRequest to JSON", e);
        }
    }

    // ===============================
    // HTTP REQUEST CONVERSION (for E2E steps)
    // ===============================

    public HttpRequest<Body> jsonToHttpRequest(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            // Try to determine the type from JSON content
            // This is a simple approach - you might need to add a type discriminator
            return objectMapper.readValue(json, new TypeReference<HttpRequest<Body>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize HttpRequest from JSON", e);
        }
    }

    public String httpRequestToJson(HttpRequest<Body> request) {
        if (request == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize HttpRequest to JSON", e);
        }
    }

    // ===============================
    // EXTRACTOR ITEMS CONVERSION
    // ===============================

    public List<ExtractorItem> jsonToExtractorItems(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<ExtractorItem>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize ExtractorItems from JSON", e);
        }
    }

    public String extractorItemsToJson(List<ExtractorItem> extractors) {
        if (extractors == null || extractors.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(extractors);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize ExtractorItems to JSON", e);
        }
    }
}
