package com.example.demo.orchestrator.domain.test.request.body;

import nu.xom.Document;

import java.util.Objects;

/**
 * XML body for HTTP requests.
 */
public class XmlBody implements Body {
    
    private Document document;

    public XmlBody() {
    }

    public XmlBody(Document document) {
        this.document = Objects.requireNonNull(document, "XML document cannot be null");
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = Objects.requireNonNull(document, "XML document cannot be null");
    }

    /**
     * Get the XML as a string.
     */
    public String toXmlString() {
        return document != null ? document.toXML() : "";
    }
}
