package com.example.demo.orchestrator.app.mapper.run;

import com.example.demo.orchestrator.domain.test.assertion.AssertionType;
import com.example.demo.orchestrator.persistence.entity.test.AssertionEntity;
import com.example.demo.orchestrator.persistence.entity.test.E2eStepEntity;
import com.example.demo.orchestrator.persistence.entity.test.E2eTestEntity;
import com.example.demo.orchestrator.persistence.entity.test.RestApiTestEntity;
import com.example.demo.orchestrator.persistence.entity.test.SoapApiTestEntity;
import com.example.demo.shared.events.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maps test entities to run request events for the Runner service.
 * Handles deserialization of JSON request data and conversion to event DTOs.
 */
@Component
public class RunRequestMapper {

    private final ObjectMapper objectMapper;

    public RunRequestMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Maps RestApiTestEntity to ApiRunRequest.
     *
     * @param testEntity the REST API test entity
     * @param runId unique identifier for this run
     * @param variables merged project + suite variables
     * @return ApiRunRequest ready to be sent to the Runner
     */
    public ApiRunRequest toApiRunRequest(RestApiTestEntity testEntity, String runId, Map<String, String> variables) {
        HttpRequest httpRequest = parseRestRequest(testEntity.getRequestJson());
        List<AssertionSpec> assertions = mapAssertions(testEntity.getAssertions());

        return new ApiRunRequest(
                runId,
                httpRequest,
                Protocol.REST,
                assertions,
                variables
        );
    }

    /**
     * Maps SoapApiTestEntity to ApiRunRequest.
     *
     * @param testEntity the SOAP API test entity
     * @param runId unique identifier for this run
     * @param variables merged project + suite variables
     * @return ApiRunRequest ready to be sent to the Runner
     */
    public ApiRunRequest toApiRunRequest(SoapApiTestEntity testEntity, String runId, Map<String, String> variables) {
        HttpRequest httpRequest = parseSoapRequest(testEntity.getRequestJson());
        List<AssertionSpec> assertions = mapAssertions(testEntity.getAssertions());

        return new ApiRunRequest(
                runId,
                httpRequest,
                Protocol.SOAP,
                assertions,
                variables
        );
    }

    /**
     * Maps E2eTestEntity to E2eRunRequest.
     *
     * @param testEntity the E2E test entity
     * @param runId unique identifier for this run
     * @param variables merged project + suite variables
     * @return E2eRunRequest ready to be sent to the Runner
     */
    public E2eRunRequest toE2eRunRequest(E2eTestEntity testEntity, String runId, Map<String, String> variables) {
        List<E2eStepRequest> stepRequests = new ArrayList<>();

        for (int i = 0; i < testEntity.getSteps().size(); i++) {
            E2eStepEntity stepEntity = testEntity.getSteps().get(i);
            E2eStepRequest stepRequest = mapE2eStep(stepEntity, i);
            stepRequests.add(stepRequest);
        }

        return new E2eRunRequest(
                runId,
                stepRequests,
                variables
        );
    }

    /**
     * Maps a single E2E step entity to E2eStepRequest.
     */
    private E2eStepRequest mapE2eStep(E2eStepEntity stepEntity, int order) {
        // Parse HTTP request from JSON
        HttpRequest httpRequest = parseHttpRequest(stepEntity.getHttpRequestJson());

        // Determine protocol (could be REST or SOAP)
        Protocol protocol = determineProtocol(httpRequest);

        // Map assertions
        List<AssertionSpec> assertions = mapAssertions(stepEntity.getAssertions());

        // Parse extractors from JSON
        List<ExtractorSpec> extractors = parseExtractors(stepEntity.getExtractorsJson());

        return new E2eStepRequest(
                String.valueOf(stepEntity.getId()),
                stepEntity.getName(),
                order,
                httpRequest,
                protocol,
                assertions,
                extractors
        );
    }

    /**
     * Parses generic HTTP request JSON (could be REST or SOAP format).
     */
    private HttpRequest parseHttpRequest(String requestJson) {
        try {
            JsonNode node = objectMapper.readTree(requestJson);

            // Check if it's SOAP (has soapAction or method is POST with XML)
            if (node.has("soapAction")) {
                return parseSoapRequest(requestJson);
            } else {
                return parseRestRequest(requestJson);
            }

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse HTTP request JSON", e);
        }
    }

    /**
     * Determines protocol from HTTP request structure.
     */
    private Protocol determineProtocol(HttpRequest httpRequest) {
        if ("SOAP".equals(httpRequest.protocol())) {
            return Protocol.SOAP;
        }
        return Protocol.REST;
    }

    /**
     * Parses extractors JSON into list of ExtractorSpec.
     * Expected JSON format: [{"name":"userId", "source":"BODY", "extractor":"JSONPATH", "expression":"$.id"}]
     */
    private List<ExtractorSpec> parseExtractors(String extractorsJson) {
        if (extractorsJson == null || extractorsJson.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            List<Map<String, String>> extractorMaps = objectMapper.readValue(
                    extractorsJson,
                    new TypeReference<List<Map<String, String>>>() {}
            );

            return extractorMaps.stream()
                    .map(map -> new ExtractorSpec(
                            map.get("name"),
                            map.get("source"),
                            map.get("extractor"),
                            map.get("expression")
                    ))
                    .collect(Collectors.toList());

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse extractors JSON", e);
        }
    }

    /**
     * Parses REST request JSON into HttpRequest DTO.
     * Expected JSON structure:
     * {
     *   "method": "GET",
     *   "url": "https://api.example.com/users",
     *   "headers": {"Content-Type": "application/json"},
     *   "queryParams": {"page": "1"},
     *   "body": {...}
     * }
     */
    private HttpRequest parseRestRequest(String requestJson) {
        try {
            JsonNode node = objectMapper.readTree(requestJson);

            String method = node.path("method").asText();
            String url = node.path("url").asText();
            Map<String, String> headers = extractHeaders(node.path("headers"));
            byte[] body = extractBody(node.path("body"));

            return new HttpRequest("REST", method, url, headers, body);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse REST request JSON", e);
        }
    }

    /**
     * Parses SOAP request JSON into HttpRequest DTO.
     * Expected JSON structure:
     * {
     *   "url": "https://api.example.com/soap",
     *   "soapAction": "http://example.com/GetUser",
     *   "headers": {"Content-Type": "text/xml"},
     *   "body": {...}
     * }
     */
    private HttpRequest parseSoapRequest(String requestJson) {
        try {
            JsonNode node = objectMapper.readTree(requestJson);

            String url = node.path("url").asText();
            Map<String, String> headers = extractHeaders(node.path("headers"));

            // SOAP always uses POST
            String method = "POST";

            // Add SOAPAction header if present
            if (node.has("soapAction")) {
                headers.put("SOAPAction", node.path("soapAction").asText());
            }

            byte[] body = extractBody(node.path("body"));

            return new HttpRequest("SOAP", method, url, headers, body);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse SOAP request JSON", e);
        }
    }

    /**
     * Extracts headers from JSON node into a simple Map<String, String>.
     * Converts multi-value headers to single values (takes first value).
     */
    private Map<String, String> extractHeaders(JsonNode headersNode) {
        Map<String, String> headers = new HashMap<>();

        if (headersNode != null && !headersNode.isNull()) {
            headersNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();

                // Handle both single values and arrays
                if (value.isArray() && value.size() > 0) {
                    headers.put(key, value.get(0).asText());
                } else if (value.isTextual()) {
                    headers.put(key, value.asText());
                }
            });
        }

        return headers;
    }

    /**
     * Extracts body from JSON node as byte array.
     * Handles different body types (JSON, XML, text).
     */
    private byte[] extractBody(JsonNode bodyNode) {
        if (bodyNode == null || bodyNode.isNull() || bodyNode.isMissingNode()) {
            return new byte[0];
        }

        try {
            // If body has a "content" field, use that
            if (bodyNode.has("content")) {
                String content = bodyNode.path("content").asText();
                return content.getBytes();
            }

            // Otherwise, serialize the entire body node
            String bodyJson = objectMapper.writeValueAsString(bodyNode);
            return bodyJson.getBytes();

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to extract body from JSON", e);
        }
    }

    /**
     * Maps domain assertion entities to event assertion specs.
     */
    private List<AssertionSpec> mapAssertions(List<AssertionEntity> assertions) {
        return assertions.stream()
                .map(this::toAssertionSpec)
                .collect(Collectors.toList());
    }

    /**
     * Converts single AssertionEntity to AssertionSpec.
     */
    private AssertionSpec toAssertionSpec(AssertionEntity entity) {
        return new AssertionSpec(
                mapAssertionType(entity.getType()),
                entity.getTarget(),
                entity.getExpected()
        );
    }

    /**
     * Maps domain AssertionType enum to string format expected by Runner.
     */
    private String mapAssertionType(AssertionType type) {
        return switch (type) {
            case STATUS_EQUALS -> "statusEquals";
            case HEADER_EQUALS -> "headerEquals";
            case BODY_CONTAINS -> "bodyContains";
            case JSONPATH_EQUALS -> "jsonPathEquals";
            case JSONPATH_EXISTS -> "jsonPathExists";
            case JSON_SCHEMA_VALID -> "jsonSchemaValid";
            case XPATH_EQUALS -> "xpathEquals";
            case XPATH_EXISTS -> "xpathExists";
            case XSD_VALID -> "xsdValid";
            case RESPONSE_TIME_LESS_THAN -> "responseTimeLessThan";
            case REGEX_MATCH -> "regexMatch";
        };
    }
}
