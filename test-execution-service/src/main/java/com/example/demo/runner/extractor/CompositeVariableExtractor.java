package com.example.demo.runner.extractor;

import com.example.demo.shared.valueobject.ExtractorSpec;

import java.net.http.HttpResponse;
import java.util.*;

/**
 * Composite extractor that delegates to specific extractors based on extractor type.
 * Automatically routes extractors to the appropriate implementation.
 */
public class CompositeVariableExtractor implements VariableExtractor {

    private final Map<String, Extractor> extractors = new HashMap<>();

    /**
     * Create composite extractor with multiple specific extractors.
     *
     * @param extractors array of specific extractors
     */
    public CompositeVariableExtractor(Extractor... extractors) {
        for (Extractor extractor : extractors) {
            for (String type : extractor.supportedTypes()) {
                this.extractors.put(type, extractor);
            }
        }
    }

    @Override
    public Map<String, String> extract(List<ExtractorSpec> specs, HttpResponse<String> response) {
        Map<String, String> variables = new HashMap<>();

        for (ExtractorSpec spec : specs) {
            Extractor extractor = extractors.get(spec.extractor());

            if (extractor == null) {
                continue;  // Skip unknown extractors
            }

            String value = extractor.extract(spec, response);
            if (value != null) {
                variables.put(spec.name(), value);
            }
        }

        return variables;
    }
}
