package com.example.demo.Runner.extractor;

import com.example.demo.shared.valueobject.ExtractorSpec;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Extracts values from XML response bodies using XPath.
 * Supports: XPATH
 */
public class XPathExtractor implements Extractor {

    private static final XPath xpath = XPathFactory.newInstance().newXPath();
    private static final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

    @Override
    public List<String> supportedTypes() {
        return List.of("XPATH");
    }

    @Override
    public String extract(ExtractorSpec spec, HttpResponse<String> response) {
        try {
            if (!"BODY".equals(spec.source())) {
                return null;
            }

            String body = response.body();
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));

            String result = xpath.evaluate(spec.expression(), doc);
            return result.isEmpty() ? null : result;

        } catch (Exception e) {
            return null;  // Extraction failed
        }
    }
}
