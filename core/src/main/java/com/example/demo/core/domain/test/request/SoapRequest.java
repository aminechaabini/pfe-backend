package com.example.demo.core.domain.test.request;

import com.example.demo.core.domain.test.request.body.XmlBody;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

/**
 * SOAP request with XML body (SOAP envelope) and SOAPAction header.
 * The XmlBody contains the complete SOAP envelope including namespaces, headers, and body.
 */
public class SoapRequest extends HttpRequest<XmlBody> {

    private static final String SOAP_11_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String SOAP_12_NS = "http://www.w3.org/2003/05/soap-envelope";

    private String soapAction;

    public SoapRequest() {
        super();
    }

    public SoapRequest(String url, XmlBody body) {
        super(HttpMethod.POST, url, body);
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    /**
     * Validates the SOAP request according to SOAP 1.1 and 1.2 specifications.
     * Checks:
     * - Base HTTP validation (URL, method)
     * - Method is POST
     * - Body (XmlBody) is not null
     * - Root element is "Envelope"
     * - Namespace is SOAP 1.1 or 1.2
     * - Body element exists in envelope
     * - For SOAP 1.1: SOAPAction header or field must be set
     * @throws IllegalStateException if validation fails
     */
    @Override
    public void validate() {
        super.validate();

        // SOAP must use POST (standard practice)
        if (getMethod() != HttpMethod.POST) {
            throw new IllegalStateException(
                "SOAP requests must use POST method"
            );
        }

        // SOAP must have a body (envelope)
        XmlBody body = getBody();
        if (body == null) {
            throw new IllegalStateException(
                "SOAP body (envelope) is required"
            );
        }

        validateSoapEnvelope(body);
    }

    /**
     * Validates the SOAP envelope structure according to W3C specifications.
     */
    private void validateSoapEnvelope(XmlBody xmlBody) {
        Document doc = xmlBody.getDocument();
        if (doc == null) {
            throw new IllegalStateException("SOAP envelope document cannot be null");
        }

        Element root = doc.getRootElement();

        // Check root element is "Envelope"
        if (!"Envelope".equals(root.getLocalName())) {
            throw new IllegalStateException(
                "SOAP root element must be 'Envelope', found: " + root.getLocalName()
            );
        }

        // Check SOAP namespace (1.1 or 1.2)
        String namespace = root.getNamespaceURI();
        boolean isSoap11 = SOAP_11_NS.equals(namespace);
        boolean isSoap12 = SOAP_12_NS.equals(namespace);

        if (!isSoap11 && !isSoap12) {
            throw new IllegalStateException(
                "Invalid SOAP namespace: " + namespace +
                ". Expected " + SOAP_11_NS + " (SOAP 1.1) or " + SOAP_12_NS + " (SOAP 1.2)"
            );
        }

        // Check Body element exists
        Elements bodyElements = root.getChildElements("Body", namespace);
        if (bodyElements.size() == 0) {
            throw new IllegalStateException(
                "SOAP Envelope must contain a Body element"
            );
        }

        // SOAP 1.1 specific: SOAPAction is mandatory (can be empty string)
        if (isSoap11 && soapAction == null) {
            throw new IllegalStateException(
                "SOAP 1.1 requires SOAPAction to be set (can be empty string)"
            );
        }
    }
}
