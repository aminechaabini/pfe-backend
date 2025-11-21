package com.example.demo.shared.context.dto.request;

import java.util.Map;

/**
 * AI output: Simple data representation of a SOAP request.
 * Maps to domain SoapRequest object.
 *
 * SOAP requests are always:
 * - POST method
 * - XML body (SOAP envelope)
 * - Content-Type: application/soap+xml (SOAP 1.2) or text/xml (SOAP 1.1)
 */
public record SoapRequestData(
    /**
     * SOAP endpoint URL.
     * Example: "http://example.com/soap/service"
     */
    String url,

    /**
     * SOAP envelope as XML string.
     * Must be a valid SOAP envelope with:
     * - Root element: Envelope
     * - Namespace: SOAP 1.1 or 1.2
     * - Body element inside envelope
     *
     * Example (SOAP 1.1):
     * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
     *   <soap:Body>
     *     <GetOrder xmlns="http://example.com">
     *       <orderId>123</orderId>
     *     </GetOrder>
     *   </soap:Body>
     * </soap:Envelope>
     *
     * Example (SOAP 1.2):
     * <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
     *   <soap:Body>
     *     <GetOrder xmlns="http://example.com">
     *       <orderId>123</orderId>
     *     </GetOrder>
     *   </soap:Body>
     * </soap:Envelope>
     */
    String soapEnvelope,

    /**
     * SOAPAction header value.
     * Required for SOAP 1.1, optional for SOAP 1.2.
     *
     * Example: "http://example.com/GetOrder"
     * Can be empty string: ""
     *
     * This is automatically added to request headers.
     */
    String soapAction,

    /**
     * Additional HTTP headers (optional).
     * Example: {"Authorization": "Bearer token"}
     *
     * Note: Content-Type and SOAPAction are handled automatically.
     * Don't include them here.
     */
    Map<String, String> additionalHeaders,

    /**
     * SOAP version: "1.1" or "1.2"
     * Default: "1.1" if not specified.
     *
     * This determines:
     * - Content-Type header (text/xml for 1.1, application/soap+xml for 1.2)
     * - Whether SOAPAction is required
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
        if (additionalHeaders == null) {
            additionalHeaders = Map.of();
        }
        if (soapVersion == null || soapVersion.isBlank()) {
            soapVersion = "1.1"; // Default to SOAP 1.1
        }
        if (!"1.1".equals(soapVersion) && !"1.2".equals(soapVersion)) {
            throw new IllegalArgumentException("SOAP version must be 1.1 or 1.2");
        }
        // SOAP 1.1 requires SOAPAction (can be empty string)
        if ("1.1".equals(soapVersion) && soapAction == null) {
            throw new IllegalArgumentException("SOAPAction required for SOAP 1.1 (can be empty string)");
        }
    }

    /**
     * Helper: Create a SOAP 1.1 request.
     */
    public static SoapRequestData soap11(String url, String envelope, String soapAction) {
        return new SoapRequestData(url, envelope, soapAction, Map.of(), "1.1");
    }

    /**
     * Helper: Create a SOAP 1.2 request.
     */
    public static SoapRequestData soap12(String url, String envelope) {
        return new SoapRequestData(url, envelope, null, Map.of(), "1.2");
    }
}
