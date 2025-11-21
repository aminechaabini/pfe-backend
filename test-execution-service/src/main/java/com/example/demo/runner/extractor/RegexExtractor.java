package com.example.demo.runner.extractor;

import com.example.demo.shared.valueobject.ExtractorSpec;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts values using regular expressions.
 * Supports: REGEX
 */
public class RegexExtractor implements Extractor {

    @Override
    public List<String> supportedTypes() {
        return List.of("REGEX");
    }

    @Override
    public String extract(ExtractorSpec spec, HttpResponse<String> response) {
        try {
            String source;

            if ("BODY".equals(spec.source())) {
                source = response.body();
            } else if ("HEADER".equals(spec.source())) {
                // For headers, expression should be the header name
                source = response.headers().firstValue(spec.expression()).orElse("");
            } else {
                return null;
            }

            Pattern pattern = Pattern.compile(spec.expression());
            Matcher matcher = pattern.matcher(source);

            if (matcher.find() && matcher.groupCount() > 0) {
                return matcher.group(1);  // Return first capture group
            }

            return null;

        } catch (Exception e) {
            return null;  // Extraction failed
        }
    }
}
