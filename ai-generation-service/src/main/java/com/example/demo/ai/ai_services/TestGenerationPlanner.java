package com.example.demo.ai.ai_services;

import com.example.demo.common.context.dto.plan.E2eTestGenerationPlan;
import com.example.demo.common.context.dto.plan.RestTestGenerationPlan;
import com.example.demo.common.context.dto.plan.SoapTestGenerationPlan;
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

          CRITICAL: totalTestCount MUST exactly match the number of items in plannedTests array.
          Count carefully!

          Provide a clear, actionable preview the user can review.
          """)
    @UserMessage("""
          Analyze this OpenAPI specification and create a test generation plan:

          Spec Content:
          {{specContent}}

          Generation Options:
          - Happy Path Tests: {{includeHappyPath}}
          - Validation Tests: {{includeValidation}}
          - Auth Tests: {{includeAuth}}
          - Error Tests: {{includeErrors}}
          - Edge Cases: {{includeEdgeCases}}

          Provide a detailed breakdown of what will be generated.
          """)
    RestTestGenerationPlan createRestTestPlan(
            @V("specType") String specType,
            @V("specContent") String specContent,
            @V("includeHappyPath") String includeHappyPath,
            @V("includeValidation") String includeValidation,
            @V("includeAuth") String includeAuth,
            @V("includeErrors") String includeErrors,
            @V("includeEdgeCases") String includeEdgeCases
    );

    @SystemMessage("""
          You are a SOAP API test planning expert. Given a WSDL specification,
          analyze and create a test generation plan showing:

          1. Suite name and description
          2. How many tests will be generated
          3. List of planned tests with names and descriptions

          CRITICAL: totalTestCount MUST exactly match the number of items in plannedTests array.
          Count carefully!

          Provide a clear, actionable preview the user can review.
          """)
    @UserMessage("""
          Analyze this WSDL specification and create a test generation plan:

          Spec Content:
          {{specContent}}

          Generation Options:
          - Happy Path Tests: {{includeHappyPath}}
          - Validation Tests: {{includeValidation}}
          - Auth Tests: {{includeAuth}}
          - Error Tests: {{includeErrors}}
          - Edge Cases: {{includeEdgeCases}}

          Provide a detailed breakdown of what will be generated.
          """)
    SoapTestGenerationPlan createSoapTestPlan(
            @V("specType") String specType,
            @V("specContent") String specContent,
            @V("includeHappyPath") String includeHappyPath,
            @V("includeValidation") String includeValidation,
            @V("includeAuth") String includeAuth,
            @V("includeErrors") String includeErrors,
            @V("includeEdgeCases") String includeEdgeCases
    );

    @SystemMessage("""
          You are an E2E workflow test planning expert. Given a workflow description
          and endpoint sequence, create a test generation plan showing:

          1. E2E test name and description
          2. How many steps will be in the workflow
          3. List of planned steps with names and descriptions

          CRITICAL: totalStepCount MUST exactly match the number of items in plannedSteps array.
          Count carefully!

          Provide a clear, actionable preview the user can review.
          """)
    @UserMessage("""
          Analyze this E2E workflow and create a test generation plan:

          Workflow Name: {{workflowName}}
          Workflow Description: {{workflowDescription}}

          Endpoint Sequence:
          {{endpointSequence}}

          Available Schemas:
          {{schemas}}

          Scenario Type: {{scenarioType}}

          Provide a detailed breakdown of what will be generated.
          """)
    E2eTestGenerationPlan createE2eTestPlan(
            @V("workflowName") String workflowName,
            @V("workflowDescription") String workflowDescription,
            @V("endpointSequence") String endpointSequence,
            @V("schemas") String schemas,
            @V("scenarioType") String scenarioType
    );
}
