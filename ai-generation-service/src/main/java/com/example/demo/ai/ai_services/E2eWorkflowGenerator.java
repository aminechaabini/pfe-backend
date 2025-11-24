package com.example.demo.ai.ai_services;

import com.example.demo.common.context.dto.e2e.CreateE2eTestRequest;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

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
    @UserMessage("""
          Generate an E2E workflow for the following:

          Workflow Name: {{workflowName}}
          Workflow Description: {{workflowDescription}}

          Endpoint Sequence:
          {{endpointSequence}}

          Available Schemas:
          {{schemas}}

          Scenario Type: {{scenarioType}}

          Generate a complete E2E test workflow with detailed steps, assertions, and variable extraction.
          """)
    CreateE2eTestRequest generateWorkflow(
            @V("workflowName") String workflowName,
            @V("workflowDescription") String workflowDescription,
            @V("endpointSequence") String endpointSequence,
            @V("schemas") String schemas,
            @V("scenarioType") String scenarioType
    );
}
