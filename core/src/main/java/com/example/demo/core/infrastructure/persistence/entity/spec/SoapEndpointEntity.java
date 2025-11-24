package com.example.demo.core.infrastructure.persistence.entity.spec;

import com.example.demo.core.domain.spec.EndpointType;
import com.example.demo.core.domain.spec.SoapVersion;
import com.example.demo.core.infrastructure.persistence.entity.project.ProjectEntity;
import com.example.demo.core.infrastructure.persistence.entity.test.TestSuiteEntity;
import jakarta.persistence.*;

import java.util.Set;

/**
 * Persistence entity for SOAP endpoints.
 * Represents a SOAP web service operation.
 *
 * Specific to SOAP:
 * - Service name (e.g., OrderService)
 * - Operation name (e.g., getOrderById)
 * - SOAP action (optional)
 * - SOAP version (1.1 or 1.2)
 */
@Entity
@DiscriminatorValue("SOAP")
public class SoapEndpointEntity extends EndpointEntity {

    @Column(name = "service_name", length = 200)
    private String serviceName;

    @Column(name = "operation_name", length = 200)
    private String operationName;

    @Column(name = "soap_action", length = 500)
    private String soapAction;

    @Enumerated(EnumType.STRING)
    @Column(name = "soap_version", length = 10)
    private SoapVersion version;

    // Constructors

    public SoapEndpointEntity() {
        super();
    }

    public SoapEndpointEntity(String summary, String operationId, String specDetails, SpecSourceEntity specSource, ProjectEntity project, Set<TestSuiteEntity> testSuites, String serviceName, String operationName, String soapAction, SoapVersion version) {
        super(summary, operationId, specDetails, specSource, project, testSuites);
        this.serviceName = serviceName;
        this.operationName = operationName;
        this.soapAction = soapAction;
        this.version = version;
    }


    // Getters and Setters

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public SoapVersion getVersion() {
        return version;
    }

    public void setVersion(SoapVersion version) {
        this.version = version;
    }

    // Implemented abstract methods

    @Override
    public EndpointType getType() {
        return EndpointType.SOAP;
    }

    @Override
    public String getUniqueKey() {
        return serviceName + ":" + operationName;
    }

    @Override
    public String toString() {
        return "SoapEndpointEntity{" +
                "id=" + getId() +
                ", serviceName='" + serviceName + '\'' +
                ", operationName='" + operationName + '\'' +
                ", version=" + version +
                ", summary='" + getSummary() + '\'' +
                ", projectId=" + (getProject() != null ? getProject().getId() : null) +
                '}';
    }
}
