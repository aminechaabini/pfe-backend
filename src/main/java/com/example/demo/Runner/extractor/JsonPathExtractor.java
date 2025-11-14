package com.example.demo.Runner.extractor;

import com.example.demo.shared.valueobject.ExtractorSpec;
import com.jayway.jsonpath.JsonPath;

import java.net.http.HttpResponse;
import java.util.List;

/**
 * Extracts values from JSON response bodies using JSONPath.
 * Supports: JSONPATH
 */
public class JsonPathExtractor implements Extractor {

    @Override
    public List<String> supportedTypes() {
        return List.of("JSONPATH");
    }

    @Override
    public String extract(ExtractorSpec spec, HttpResponse<String> response) {
        try {
            if (!"BODY".equals(spec.source())) {
                return null;
            }

            Object value = JsonPath.read(response.body(), spec.expression());
            return String.valueOf(value);

        } catch (Exception e) {
            return null;  // Extraction failed
        }
    }
}
