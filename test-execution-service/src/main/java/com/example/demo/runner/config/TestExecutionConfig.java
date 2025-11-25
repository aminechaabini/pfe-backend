package com.example.demo.runner.config;

import com.example.demo.common.ports.TestExecutionPort;
import com.example.demo.runner.*;
import com.example.demo.runner.builder.*;
import com.example.demo.runner.executor.*;
import com.example.demo.runner.extractor.*;
import com.example.demo.runner.validator.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration class for the test execution service.
 * Wires up all dependencies and exposes the TestExecutionPort.
 */
@Configuration
public class TestExecutionConfig {

    @Bean
    public HttpRequestExecutor httpRequestExecutor() {
        return new DefaultHttpRequestExecutor();
    }

    @Bean
    public Map<String, HttpRequestBuilder> httpRequestBuilders() {
        return Map.of(
            "REST", new RestRequestBuilder(),
            "SOAP", new SoapRequestBuilder()
        );
    }

    @Bean
    public AssertionValidator assertionValidator() {
        return new CompositeAssertionValidator(
            new StatusAssertionValidator(),
            new JsonPathAssertionValidator(),
            new XPathAssertionValidator()
        );
    }

    @Bean
    public VariableExtractor variableExtractor() {
        return new CompositeVariableExtractor(
            new JsonPathExtractor(),
            new XPathExtractor(),
            new RegexExtractor()
        );
    }

    @Bean
    public ApiTestRunner apiTestRunner(
        Map<String, HttpRequestBuilder> builders,
        HttpRequestExecutor executor,
        AssertionValidator validator
    ) {
        return new ApiTestRunner(builders, executor, validator);
    }

    @Bean
    public E2eTestRunner e2eTestRunner(
        Map<String, HttpRequestBuilder> builders,
        HttpRequestExecutor executor,
        AssertionValidator validator,
        VariableExtractor extractor
    ) {
        return new E2eTestRunner(builders, executor, validator, extractor);
    }

    @Bean
    public TestExecutionPort testExecutionPort(
        ApiTestRunner apiRunner,
        E2eTestRunner e2eRunner
    ) {
        return new RunnerService(apiRunner, e2eRunner);
    }
}
