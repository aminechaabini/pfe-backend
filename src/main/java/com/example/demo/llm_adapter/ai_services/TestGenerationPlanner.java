package com.example.demo.llm_adapter.ai_services;

import com.example.demo.llm_adapter.dto.plan.RestTestGenerationPlan;
import com.example.demo.llm_adapter.dto.plan.SoapTestGenerationPlan;
import com.example.demo.llm_adapter.dto.plan.E2eTestGenerationPlan;
import com.example.demo.orchestrator.dto.TestPlanningContext;
import com.example.demo.orchestrator.dto.E2eGenerationContext;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface TestGenerationPlanner {

    @SystemMessage("""
          You are a REST API test planning expert. Given an OpenAPI specification,
          analyze and create a test generation plan showing:

          1. Suite name and description
          2. How many tests will be generated
          3. List of planned tests with names and descriptions

          Provide a clear, actionable preview the user can review.
          """)
    @UserMessage("""
          Analyze this OpenAPI specification and create a test generation plan:

          Spec Content:
          {{context.specContent}}

          Generation Options:
          - Happy Path Tests: {{context.includeHappyPath}}
          - Validation Tests: {{context.includeValidation}}
          - Auth Tests: {{context.includeAuth}}
          - Error Tests: {{context.includeErrors}}
          - Edge Cases: {{context.includeEdgeCases}}

          Provide a detailed breakdown of what will be generated.
          """)
    RestTestGenerationPlan createRestTestPlan(@V("context") TestPlanningContext context);

    @SystemMessage("""
          You are a SOAP API test planning expert. Given a WSDL specification,
          analyze and create a test generation plan showing:

          1. Suite name and description
          2. How many tests will be generated
          3. List of planned tests with names and descriptions

          Provide a clear, actionable preview the user can review.
          """)
    @UserMessage("""
          Analyze this WSDL specification and create a test generation plan:

          Spec Content:
          {{context.specContent}}

          Generation Options:
          - Happy Path Tests: {{context.includeHappyPath}}
          - Validation Tests: {{context.includeValidation}}
          - Auth Tests: {{context.includeAuth}}
          - Error Tests: {{context.includeErrors}}
          - Edge Cases: {{context.includeEdgeCases}}

          Provide a detailed breakdown of what will be generated.
          """)
    SoapTestGenerationPlan createSoapTestPlan(@V("context") TestPlanningContext context);

    @SystemMessage("""
          You are an E2E workflow test planning expert. Given a workflow description
          and endpoint sequence, create a test generation plan showing:

          1. E2E test name and description
          2. How many steps will be in the workflow
          3. List of planned steps with names and descriptions

          Provide a clear, actionable preview the user can review.
          """)
    @UserMessage("""
          Analyze this E2E workflow and create a test generation plan:

          Workflow Name: {{context.workflowName}}
          Workflow Description: {{context.workflowDescription}}

          Endpoint Sequence:
          {{context.endpointSequence}}

          Available Schemas:
          {{context.schemas}}

          Scenario Type: {{context.scenarioType}}

          Provide a detailed breakdown of what will be generated.
          """)
    E2eTestGenerationPlan createE2eTestPlan(@V("context") E2eGenerationContext context);
}
