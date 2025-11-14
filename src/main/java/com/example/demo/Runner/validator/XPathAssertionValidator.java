package com.example.demo.Runner.validator;

import com.example.demo.shared.valueobject.AssertionResult;
import com.example.demo.shared.valueobject.AssertionSpec;
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
 * Validates XPath assertions against XML response bodies.
 * Supports: xpathExists, xpathEquals
 */
public class XPathAssertionValidator implements Validator {

    private static final XPath xpath = XPathFactory.newInstance().newXPath();
    private static final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

    @Override
    public List<String> supportedTypes() {
        return List.of("xpathExists", "xpathEquals");
    }

    @Override
    public AssertionResult validate(AssertionSpec assertion, HttpResponse<String> response) {
        try {
            String body = response.body();
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));

            String result = xpath.evaluate(assertion.expr(), doc);

            if ("xpathExists".equals(assertion.type())) {
                boolean exists = result != null && !result.isEmpty();
                String message = exists
                    ? "Path exists: " + assertion.expr()
                    : "Path does not exist: " + assertion.expr();
                return new AssertionResult(assertion, exists, message);
            }

            // xpathEquals
            boolean passed = assertion.expected().equals(result);
            String message = passed
                ? "Value matches: " + result
                : "Expected " + assertion.expected() + " but got " + result;

            return new AssertionResult(assertion, passed, message);

        } catch (Exception e) {
            return new AssertionResult(assertion, false, "XPath error: " + e.getMessage());
        }
    }
}
