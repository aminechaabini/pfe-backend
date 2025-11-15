package com.example.demo.orchestrator.domain.spec;

public class SoapEndpoint extends Endpoint {
    // SOAP-specific fields
    public String serviceName;    // OrderService
    public String operationName;  // getOrderById
    public String soapAction;     // optional
    public SoapVersion version;   // 1.1 or 1.2

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
