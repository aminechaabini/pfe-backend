package com.example.demo.orchestrator.domain.test;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * REST-specific fields live here; id comes from the base table (JOINED inheritance),
 * so no @Id/@GeneratedValue in this subclass.
 */
@Entity
@Table(name = "rest_api_tests")
@DiscriminatorValue("REST")
public class RESTAPITest extends APITest {

    @NotBlank
    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod; // e.g., GET, POST

    @NotBlank
    @Column(name = "url", nullable = false, length = 2000)
    private String url;        // can be relative; env/baseUrl resolves it

    @Lob
    @Column(name = "headers_json")
    private String headersJson;  // JSON map (string for portability)

    @Lob
    @Column(name = "query_json")
    private String queryJson;    // JSON map (string)

    @Lob
    @Column(name = "body")
    private String body;         // raw request body

    @Lob
    @Column(name = "assertions_json")
    private String assertionsJson; // JSON array of assertions (status/jsonpath/etc.)

    protected RESTAPITest() { }

    public RESTAPITest(TestSuite suite, String name, String description,
                       String httpMethod, String url,
                       String headersJson, String queryJson,
                       String body, String assertionsJson) {
        super(suite, name, description);
        this.httpMethod = httpMethod;
        this.url = url;
        this.headersJson = headersJson;
        this.queryJson = queryJson;
        this.body = body;
        this.assertionsJson = assertionsJson;
    }

    // Getters/setters
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getHeadersJson() { return headersJson; }
    public void setHeadersJson(String headersJson) { this.headersJson = headersJson; }

    public String getQueryJson() { return queryJson; }
    public void setQueryJson(String queryJson) { this.queryJson = queryJson; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getAssertionsJson() { return assertionsJson; }
    public void setAssertionsJson(String assertionsJson) { this.assertionsJson = assertionsJson; }
}
