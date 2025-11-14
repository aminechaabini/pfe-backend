package com.example.demo.Runner.extractor;

import com.example.demo.shared.valueobject.ExtractorSpec;

import java.net.http.HttpResponse;
import java.util.List;

/**
 * Base interface for specific variable extractors.
 * Each extractor supports one or more extraction types.
 */
public interface Extractor {

    /**
     * Get list of extraction types this extractor supports.
     *
     * @return list of supported types (e.g., ["JSONPATH"], ["XPATH"])
     */
    List<String> supportedTypes();

    /**
     * Extract a value from the response.
     *
     * @param spec extraction specification
     * @param response the HTTP response
     * @return extracted value as string, or null if extraction failed
     */
    String extract(ExtractorSpec spec, HttpResponse<String> response);
}
