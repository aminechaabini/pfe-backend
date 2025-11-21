package com.example.demo.runner.builder;

import com.example.demo.shared.valueobject.HttpRequestData;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds REST HTTP requests with variable substitution.
 * Supports ${var} and {var} template syntax.
 */
public class RestRequestBuilder implements HttpRequestBuilder {

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{([^}]+)}|\\{([^}]+)}");

    @Override
    public HttpRequest build(HttpRequestData requestData, Map<String, String> variables) {
        Map<String, String> vars = variables == null ? Collections.emptyMap() : variables;

        // Resolve URL
        String url = resolveTemplates(requestData.url(), vars);

        // Resolve headers
        String[] headers = resolveHeaders(requestData.headers(), vars);

        // Resolve body
        HttpRequest.BodyPublisher bodyPublisher = resolveBody(
            requestData.method(),
            requestData.body(),
            vars
        );

        return HttpRequest.newBuilder()
            .uri(URI.create(url))
            .headers(headers)
            .method(requestData.method(), bodyPublisher)
            .build();
    }

    private String[] resolveHeaders(Map<String, String> headers, Map<String, String> vars) {
        if (headers == null || headers.isEmpty()) {
            return new String[0];
        }

        return headers.entrySet().stream()
            .flatMap(e -> java.util.stream.Stream.of(
                e.getKey(),
                resolveTemplates(e.getValue(), vars)
            ))
            .toArray(String[]::new);
    }

    private HttpRequest.BodyPublisher resolveBody(String method, byte[] body, Map<String, String> vars) {
        // GET, DELETE, HEAD shouldn't have body
        boolean isNoBodyMethod = method.equals("GET") || method.equals("DELETE") || method.equals("HEAD");

        if (isNoBodyMethod || body == null || body.length == 0) {
            return HttpRequest.BodyPublishers.noBody();
        }

        // Try to resolve templates in body
        try {
            String bodyText = new String(body, StandardCharsets.UTF_8);
            if (TEMPLATE_PATTERN.matcher(bodyText).find()) {
                String resolved = resolveTemplates(bodyText, vars);
                return HttpRequest.BodyPublishers.ofString(resolved);
            }
        } catch (Exception e) {
            // If resolution fails, use original bytes
        }

        return HttpRequest.BodyPublishers.ofByteArray(body);
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
