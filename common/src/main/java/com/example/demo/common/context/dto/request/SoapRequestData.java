package com.example.demo.common.context.dto.request;

import java.util.List;

/**
 * AI output: Simple data representation of a SOAP request.
 * Maps to domain SoapRequest object.
 *
 * Note: All fields are required except auth (use empty lists instead of null).
 * This ensures proper JSON schema generation for LangChain4j.
 */
public record SoapRequestData(
    /**
     * SOAP endpoint URL (required).
     * Example: "http://example.com/soap/service"
     */
    String url,

    /**
     * SOAP envelope as XML string (required).
     */
    String soapEnvelope,

    /**
     * SOAPAction header value (required, can be empty string "").
     * Required for SOAP 1.1, optional for SOAP 1.2.
     */
    String soapAction,

    /**
     * Additional HTTP headers (required, use empty list if none).
     * Example: [{"key": "Authorization", "value": "Bearer token"}]
     */
    List<Header> additionalHeaders,

    /**
     * SOAP version (required): "1.1" or "1.2"
     */
    String soapVersion
) {
    public SoapRequestData {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("SOAP endpoint URL required");
        }
        if (soapEnvelope == null || soapEnvelope.isBlank()) {
            throw new IllegalArgumentException("SOAP envelope required");
        }
        if (soapAction == null) {
            throw new IllegalArgumentException("SOAPAction cannot be null (use empty string if not needed)");
        }
        if (additionalHeaders == null) {
            throw new IllegalArgumentException("AdditionalHeaders cannot be null (use empty list)");
        }
        if (soapVersion == null || soapVersion.isBlank()) {
            throw new IllegalArgumentException("SOAP version required");
        }
        if (!"1.1".equals(soapVersion) && !"1.2".equals(soapVersion)) {
            throw new IllegalArgumentException("SOAP version must be 1.1 or 1.2");
        }
        // SOAP 1.1 requires SOAPAction (can be empty string)
        if ("1.1".equals(soapVersion) && soapAction.isBlank()) {
            throw new IllegalArgumentException("SOAPAction required for SOAP 1.1 (can be empty string \"\")");
        }
    }

    /**
     * Helper: Create a SOAP 1.1 request.
     */
    public static SoapRequestData soap11(String url, String envelope, String soapAction) {
        return new SoapRequestData(url, envelope, soapAction, List.of(), "1.1");
    }

    /**
     * Helper: Create a SOAP 1.2 request.
     */
    public static SoapRequestData soap12(String url, String envelope) {
        return new SoapRequestData(url, envelope, "", List.of(), "1.2");
    }
}
