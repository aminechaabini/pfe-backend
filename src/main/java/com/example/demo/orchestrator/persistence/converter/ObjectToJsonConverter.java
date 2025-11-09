package com.example.demo.orchestrator.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;

/**
 * Generic JPA Converter for any Object to JSON string.
 * Useful for storing complex objects like request bodies, auth objects, etc.
 *
 * Note: This stores the object as JSON but loses type information on retrieval.
 * For type-safe conversion, create specific converters for each type.
 *
 * Usage:
 * @Convert(converter = ObjectToJsonConverter.class)
 * @Column(columnDefinition = "TEXT")
 * private Object requestBody;
 */
@Converter
public class ObjectToJsonConverter implements AttributeConverter<Object, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting object to JSON", e);
        }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, Object.class);
        } catch (IOException e) {
            // Log the error and return null instead of failing
            return null;
        }
    }
}
