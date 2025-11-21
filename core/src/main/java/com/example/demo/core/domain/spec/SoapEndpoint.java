package com.example.demo.core.domain.spec;

public class SoapEndpoint extends Endpoint {
    // SOAP-specific fields
    private String serviceName;    // OrderService
    private String operationName;  // getOrderById
    private String soapAction;     // optional
    private SoapVersion version;   // 1.1 or 1.2

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
