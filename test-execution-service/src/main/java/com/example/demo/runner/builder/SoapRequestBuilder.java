package com.example.demo.runner.builder;

import com.example.demo.shared.valueobject.HttpRequestData;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds SOAP HTTP requests with variable substitution.
 * Always uses POST method and adds Content-Type for SOAP.
 */
public class SoapRequestBuilder implements HttpRequestBuilder {

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{([^}]+)}|\\{([^}]+)}");

    @Override
    public HttpRequest build(HttpRequestData requestData, Map<String, String> variables) {
        Map<String, String> vars = variables == null ? Collections.emptyMap() : variables;

        // Resolve URL
        String url = resolveTemplates(requestData.url(), vars);

        // Resolve headers (add SOAP-specific headers)
        String[] headers = resolveHeaders(requestData.headers(), vars);

        // Resolve body (SOAP always has body - the envelope)
        HttpRequest.BodyPublisher bodyPublisher = resolveBody(requestData.body(), vars);

        return HttpRequest.newBuilder()
            .uri(URI.create(url))
            .headers(headers)
            .POST(bodyPublisher)  // SOAP always uses POST
            .build();
    }

    private String[] resolveHeaders(Map<String, String> headers, Map<String, String> vars) {
        Map<String, String> resolvedHeaders = new LinkedHashMap<>();

        // Add user headers
        if (headers != null) {
            headers.forEach((k, v) -> resolvedHeaders.put(k, resolveTemplates(v, vars)));
        }

        // Ensure Content-Type for SOAP (if not already set)
        resolvedHeaders.putIfAbsent("Content-Type", "text/xml; charset=utf-8");

        return resolvedHeaders.entrySet().stream()
            .flatMap(e -> java.util.stream.Stream.of(e.getKey(), e.getValue()))
            .toArray(String[]::new);
    }

    private HttpRequest.BodyPublisher resolveBody(byte[] body, Map<String, String> vars) {
        if (body == null || body.length == 0) {
            throw new IllegalArgumentException("SOAP request must have a body (envelope)");
        }

        // Resolve templates in SOAP envelope
        String bodyText = new String(body, StandardCharsets.UTF_8);
        String resolved = resolveTemplates(bodyText, vars);

        return HttpRequest.BodyPublishers.ofString(resolved);
    }

    private String resolveTemplates(String input, Map<String, String> vars) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        Matcher m = TEMPLATE_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            String varName = (m.group(1) != null) ? m.group(1) : m.group(2);
            String replacement = vars.getOrDefault(varName, "");
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        m.appendTail(sb);
        return sb.toString();
    }
}
