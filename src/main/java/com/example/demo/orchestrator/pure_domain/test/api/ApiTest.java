package com.example.demo.orchestrator.pure_domain.test.api;

import com.example.demo.orchestrator.pure_domain.test.RunnableType;
import com.example.demo.orchestrator.pure_domain.test.TestCase;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class ApiTest extends TestCase {
    private String HttpHeaders;
    private String assertions;
    private String url;

    public ApiTest(Long id, String name, String description, RunnableType type) {
        super(id, name, description, type);
    }

    public String getHttpHeaders() {
        return HttpHeaders;
    }

    public String getAssertions() {
        return assertions;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) throws MalformedURLException, URISyntaxException {
        if (isValidURL(url)) this.url = url;
        else throw new MalformedURLException();
    }

    boolean isValidURL(String url) throws MalformedURLException, URISyntaxException {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }
    public void setHttpHeaders(String httpHeaders) {
        // verify headers
    }

    public void set
}
