package com.example.demo.runner.extractor;

import com.example.demo.shared.valueobject.ExtractorSpec;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * Extracts variables from HTTP responses based on ExtractorSpecs.
 */
public interface VariableExtractor {

    /**
     * Extract variables from response.
     *
     * @param extractors specifications for what to extract
     * @param response the HTTP response to extract from
     * @return map of variable names to extracted values
     */
    Map<String, String> extract(List<ExtractorSpec> extractors, HttpResponse<String> response);
}
