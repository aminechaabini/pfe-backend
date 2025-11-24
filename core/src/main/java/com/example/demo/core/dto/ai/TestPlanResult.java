package com.example.demo.core.dto.ai;

import com.example.demo.common.context.dto.plan.RestTestGenerationPlan;
import com.example.demo.common.context.dto.plan.SoapTestGenerationPlan;
import com.example.demo.common.context.dto.plan.E2eTestGenerationPlan;

/**
 * Result wrapper for test generation plan from AI.
 * Can contain REST, SOAP, or E2E plan.
 */
public record TestPlanResult(
    RestTestGenerationPlan restPlan,
    SoapTestGenerationPlan soapPlan,
    E2eTestGenerationPlan e2ePlan
) {
    public TestPlanResult {
        int count = 0;
        if (restPlan != null) count++;
        if (soapPlan != null) count++;
        if (e2ePlan != null) count++;

        if (count == 0) {
            throw new IllegalArgumentException("At least one plan must be provided");
        }
        if (count > 1) {
            throw new IllegalArgumentException("Only one plan type can be provided");
        }
    }

    public static TestPlanResult forRest(RestTestGenerationPlan plan) {
        return new TestPlanResult(plan, null, null);
    }

    public static TestPlanResult forSoap(SoapTestGenerationPlan plan) {
        return new TestPlanResult(null, plan, null);
    }

    public static TestPlanResult forE2e(E2eTestGenerationPlan plan) {
        return new TestPlanResult(null, null, plan);
    }

    public boolean isRest() {
        return restPlan != null;
    }

    public boolean isSoap() {
        return soapPlan != null;
    }

    public boolean isE2e() {
        return e2ePlan != null;
    }
}
