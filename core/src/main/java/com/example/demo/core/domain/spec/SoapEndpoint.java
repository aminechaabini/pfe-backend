package com.example.demo.core.domain.spec;

import java.time.Instant;

public class SoapEndpoint extends Endpoint {
    // SOAP-specific fields
    private String serviceName;    // OrderService
    private String operationName;  // getOrderById
    private String soapAction;     // optional
    private SoapVersion version;   // 1.1 or 1.2

    /**
     * Create new SoapEndpoint from parsed specification data.
     */
    public static SoapEndpoint create(
            String serviceName,
            String operationName,
            String summary,
            String specDetails,
            SoapVersion version,
            String soapAction,
            Long specSourceId,
            Long projectId) {

        SoapEndpoint endpoint = new SoapEndpoint();
        endpoint.serviceName = serviceName;
        endpoint.operationName = operationName;
        endpoint.name = operationName;
        endpoint.summary = summary;
        endpoint.operationId = serviceName + "." + operationName;
        endpoint.specDetails = specDetails;
        endpoint.version = version;
        endpoint.soapAction = soapAction;
        endpoint.specSourceId = specSourceId;
        endpoint.projectId = projectId;
        endpoint.createdAt = Instant.now();
        endpoint.updatedAt = Instant.now();
        return endpoint;
    }

    /**
     * Reconstitute SoapEndpoint from persistence (use in mappers only).
     */
    public static SoapEndpoint reconstitute(
            Long id,
            String serviceName,
            String operationName,
            SoapVersion soapVersion,
            String soapAction,
            String summary,
            String operationId,
            Long specSourceId,
            Long projectId,
            Instant createdAt,
            Instant updatedAt) {

        SoapEndpoint endpoint = new SoapEndpoint();
        endpoint.id = id;
        endpoint.serviceName = serviceName;
        endpoint.operationName = operationName;
        endpoint.version = soapVersion;
        endpoint.soapAction = soapAction;
        endpoint.summary = summary;
        endpoint.operationId = operationId;
        endpoint.specSourceId = specSourceId;
        endpoint.projectId = projectId;
        endpoint.createdAt = createdAt;
        endpoint.updatedAt = updatedAt;
        return endpoint;
    }

    // Implementations from spec
    @Override
    public String getDisplayName() {
        return serviceName + "." + operationName;
    }

    @Override
    public String getUniqueKey() {
        return serviceName + ":" + operationName;
    }

    @Override
    public EndpointType getType() {
        return EndpointType.SOAP;
    }

    @Override
    public boolean hasPathParameters() {
        return false; // SOAP doesn't have path params
    }

    // Getters
    public String getServiceName() {
        return serviceName;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public SoapVersion getVersion() {
        return version;
    }

    // SOAP-specific methods (minimal implementations)
    public String getBinding() {
        return soapAction;
    }

    public boolean isDocumentStyle() {
        return false;
    }

    public boolean isRpcStyle() {
        return false;
    }
}
