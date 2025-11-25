package com.example.demo.core.infrastructure.config;

import com.example.demo.core.application.ports.TestExecutionPort;
import com.example.demo.core.infrastructure.adapter.TestExecutionAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Infrastructure layer configuration.
 * Wires up adapters to external services.
 */
@Configuration
public class InfrastructureConfig {

    /**
     * Create TestExecutionPort adapter that delegates to test-execution-service.
     *
     * @param testExecutionService The low-level test execution service port from common module
     * @return Test execution port implementation for core
     */
    @Bean
    public TestExecutionPort testExecutionPort(
        com.example.demo.common.ports.TestExecutionPort testExecutionService
    ) {
        return new TestExecutionAdapter(testExecutionService);
    }
}
