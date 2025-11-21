package com.example.demo.core.infrastructure.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JPA Converter for Map<String, List<String>> to JSON string.
 * Useful for storing HTTP headers as JSON in the database.
 *
 * Usage:
 * @Convert(converter = HeadersToJsonConverter.class)
 * @Column(columnDefinition = "TEXT")
 * private Map<String, List<String>> headers;
 */
@Converter
public class HeadersToJsonConverter implements AttributeConverter<Map<String, List<String>>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, List<String>> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting headers to JSON", e);
        }
    }

    @Override
    public Map<String, List<String>> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<String, List<String>>>() {});
        } catch (IOException e) {
            // Log the error and return empty map instead of failing
            return new HashMap<>();
        }
    }
}
