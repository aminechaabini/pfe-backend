package com.example.demo.ai.ai_services;

import com.example.demo.shared.context.dto.e2e.CreateE2eTestRequest;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface E2eWorkflowGenerator {

    @SystemMessage("""
          You are an E2E test generation expert. Given a sequence of API endpoints,
          generate a comprehensive E2E test workflow.

          For each step, provide:
          1. Request configuration (method, URL, body, headers)
          2. Assertions to validate the response
          3. Variables to extract for use in subsequent steps

          Consider dependencies between steps and data flow.
          Steps can be REST or SOAP calls (mixed workflows supported).
          """)
    @UserMessage("{context}")
    CreateE2eTestRequest generateWorkflow(String context);
}
