package com.example.demo.runner.builder;

import com.example.demo.shared.events.HttpRequest;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RestBuilder  implements RequestBuilder {

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{([^}]+)}|\\{([^}]+)}");

    @Override
    public HttpRequest build(HttpRequest request, Map<String, String> variables){
        if (request == null) throw new IllegalArgumentException("request required");
        Map<String, String> vars = variables == null ? Collections.emptyMap() : variables;

        // resolve simple string fields
        String protocol = resolveTemplates(request.protocol(), vars);
        String method = resolveTemplates(request.method(), vars);
        String url = resolveTemplates(request.url(), vars);

        // resolve headers (values only)
        Map<String, String> headers = resolveHeaders(request.headers(), vars);

        // resolve body if textual (attempt UTF-8 decode/encode). If decode fails, keep original bytes.
        byte[] body = resolveBody(request.body(), vars);

        return new HttpRequest(protocol, method, url, headers, body);

    }

    private Map<String, String> resolveHeaders(Map<String, String> headers, Map<String, String> vars) {
        if (headers == null || headers.isEmpty()) return Collections.emptyMap();
        Map<String, String> out = new LinkedHashMap<>(headers.size());
        for (Map.Entry<String, String> e : headers.entrySet()) {
            String key = e.getKey();
            String val = e.getValue();
            String resolvedVal = resolveTemplates(val, vars);
            out.put(key, resolvedVal);
        }
        return out;
    }

    private byte[] resolveBody(byte[] bodyBytes, Map<String, String> vars) {
        if (bodyBytes == null || bodyBytes.length == 0) return bodyBytes;

        // try decode as UTF-8 text
        try {
            String bodyText = new String(bodyBytes, StandardCharsets.UTF_8);
            // only attempt resolution if the body contains a template token to avoid unnecessary work
            if (TEMPLATE_PATTERN.matcher(bodyText).find()) {
                String resolved = resolveTemplates(bodyText, vars);
                return resolved.getBytes(StandardCharsets.UTF_8);
            } else {
                return bodyBytes;
            }
        } catch (Exception ex) {
            // if anything goes wrong decoding, return original bytes (treat as binary)
            return bodyBytes;
        }
    }

    /**
     * Replace template occurrences with values from the variables map.
     * Supports both ${name} and {name}.
     * Missing variables are replaced with empty string.
     */
    private String resolveTemplates(String input, Map<String, String> vars) {
        if (input == null || input.isEmpty()) return input;
        Matcher m = TEMPLATE_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String name = (m.group(1) != null) ? m.group(1) : m.group(2);
            String replacement = vars.getOrDefault(name, "");
            // avoid accidental regex/replacement interpretation
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
