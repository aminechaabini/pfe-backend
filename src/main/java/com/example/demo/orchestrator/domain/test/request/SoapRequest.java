package com.example.demo.orchestrator.domain.test.request;

import com.example.demo.orchestrator.domain.test.request.body.XmlBody;

import java.util.*;

/**
 * SOAP request with envelope namespaces, SOAP headers, and SOAP body.
 * Note: Consider using more specific types instead of Object for headers and body.
 */
public class SoapRequest extends HttpRequest<XmlBody> {

    private Map<String, String> envelopeNamespaces;
    private Map<String, String> soapHeaders; // Changed from Object to String for better type safety
    private Map<String, String> soapBody;    // Changed from Object to String for better type safety

    public SoapRequest() {
        super();
        this.envelopeNamespaces = new HashMap<>();
        this.soapHeaders = new HashMap<>();
        this.soapBody = new HashMap<>();
    }

    public SoapRequest(String url, XmlBody body) {
        super(HttpMethod.POST, url, body); // SOAP is typically POST
        this.envelopeNamespaces = new HashMap<>();
        this.soapHeaders = new HashMap<>();
        this.soapBody = new HashMap<>();
    }

    // Getters
    public Map<String, String> getEnvelopeNamespaces() {
        return Collections.unmodifiableMap(envelopeNamespaces);
    }

    public Map<String, String> getSoapHeaders() {
        return Collections.unmodifiableMap(soapHeaders);
    }

    public Map<String, String> getSoapBody() {
        return Collections.unmodifiableMap(soapBody);
    }

    // Namespace operations
    public void addEnvelopeNamespace(String prefix, String uri) {
        Objects.requireNonNull(prefix, "Namespace prefix cannot be null");
        Objects.requireNonNull(uri, "Namespace URI cannot be null");
        this.envelopeNamespaces.put(prefix, uri);
    }

    public void removeEnvelopeNamespace(String prefix) {
        this.envelopeNamespaces.remove(prefix);
    }

    // SOAP header operations
    public void addSoapHeader(String name, String value) {
        Objects.requireNonNull(name, "SOAP header name cannot be null");
        Objects.requireNonNull(value, "SOAP header value cannot be null");
        this.soapHeaders.put(name, value);
    }

    public void removeSoapHeader(String name) {
        this.soapHeaders.remove(name);
    }

    // SOAP body operations
    public void addSoapBodyElement(String name, String value) {
        Objects.requireNonNull(name, "SOAP body element name cannot be null");
        Objects.requireNonNull(value, "SOAP body element value cannot be null");
        this.soapBody.put(name, value);
    }

    public void removeSoapBodyElement(String name) {
        this.soapBody.remove(name);
    }
}
