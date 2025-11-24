package com.example.demo.core.infrastructure.spec;

import com.example.demo.core.application.parser.SpecParser;
import com.example.demo.core.application.parser.SpecParseException;
import com.example.demo.core.application.parser.dto.ParsedEndpoint;
import com.example.demo.core.application.parser.dto.ParsedSoapEndpoint;
import com.example.demo.core.application.parser.dto.ParsedSpec;
import com.example.demo.core.domain.spec.SpecType;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.wsdl.*;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parser for SOAP-based API specifications.
 * Handles WSDL 1.1 and WSDL 2.0.
 *
 * Uses the wsdl4j library which supports both WSDL versions.
 */
@Component
public class SoapSpecParser implements SpecParser {

    @Override
    public ParsedSpec parse(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content cannot be null or blank");
        }

        try {
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            reader.setFeature("javax.wsdl.importDocuments", true);

            Definition definition = reader.readWSDL(null, new InputSource(new StringReader(content)));

            String version = detectWsdlVersion(definition);
            List<ParsedEndpoint> endpoints = extractOperations(definition);

            return new ParsedSpec(version, endpoints);

        } catch (WSDLException e) {
            throw new SpecParseException("Failed to parse WSDL: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(SpecType specType) {
        return specType != null && specType.isSoap();
    }

    private String detectWsdlVersion(Definition definition) {
        // Check target namespace for version hints
        String targetNamespace = definition.getTargetNamespace();

        if (targetNamespace != null) {
            if (targetNamespace.contains("wsdl20") || targetNamespace.contains("2006/01")) {
                return "2.0";
            }
        }

        // Default to WSDL 1.1 (most common)
        return "1.1";
    }

    @SuppressWarnings("unchecked")
    private List<ParsedEndpoint> extractOperations(Definition definition) {
        List<ParsedEndpoint> endpoints = new ArrayList<>();

        if (definition.getServices() == null) {
            return endpoints;
        }

        Map<String, Service> services = (Map<String, Service>) definition.getServices();

        services.values().forEach(service -> {
            String serviceName = service.getQName().getLocalPart();
            extractOperationsFromService(service, serviceName, endpoints);
        });

        return endpoints;
    }

    @SuppressWarnings("unchecked")
    private void extractOperationsFromService(Service service, String serviceName,
                                              List<ParsedEndpoint> endpoints) {
        if (service.getPorts() == null) {
            return;
        }

        Map<String, Port> ports = (Map<String, Port>) service.getPorts();

        ports.values().forEach(port -> {
            Binding binding = port.getBinding();
            if (binding != null) {
                extractOperationsFromBinding(binding, serviceName, endpoints);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void extractOperationsFromBinding(Binding binding, String serviceName,
                                              List<ParsedEndpoint> endpoints) {
        if (binding.getBindingOperations() == null) {
            return;
        }

        List<BindingOperation> bindingOperations = (List<BindingOperation>) binding.getBindingOperations();

        bindingOperations.forEach(bindingOp -> {
            String operationName = bindingOp.getName();
            Operation operation = bindingOp.getOperation();

            String summary = extractDocumentation(operation);
            String operationDetails = buildOperationDetails(operation);

            ParsedSoapEndpoint endpoint = new ParsedSoapEndpoint(
                serviceName,
                operationName,
                summary,
                operationDetails
            );

            endpoints.add(endpoint);
        });
    }

    private String extractDocumentation(Operation operation) {
        if (operation == null || operation.getDocumentationElement() == null) {
            return "";
        }

        return operation.getDocumentationElement().getTextContent();
    }

    private String buildOperationDetails(Operation operation) {
        if (operation == null) {
            return "{}";
        }

        // Build simple JSON representation of operation
        StringBuilder json = new StringBuilder("{");

        if (operation.getInput() != null) {
            String inputMsg = operation.getInput().getMessage() != null
                ? operation.getInput().getMessage().getQName().getLocalPart()
                : "";
            json.append("\"input\":\"").append(inputMsg).append("\",");
        }

        if (operation.getOutput() != null) {
            String outputMsg = operation.getOutput().getMessage() != null
                ? operation.getOutput().getMessage().getQName().getLocalPart()
                : "";
            json.append("\"output\":\"").append(outputMsg).append("\"");
        }

        json.append("}");

        return json.toString();
    }
}
