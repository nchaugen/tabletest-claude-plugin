package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @DisplayName("Decides the insurance application outcome and premium")
    @Description("""
        Age 760 is not a realistic applicant age; it is used only to force an
        internal risk score above the rejection threshold, so that row can show
        that the no-claims renewal auto-approval is evaluated before, and
        overrides, the risk-based rejection.
        """)
    @TableTest("""
        Scenario                                         | Applicant Type | Age | Claim Count | Decision?     | Premium?
        Renewal with no claims                           | RENEWAL        | 40  | 0           | AUTO_APPROVED | 0
        Renewal with no claims overrides high risk score | RENEWAL        | 760 | 0           | AUTO_APPROVED | 0
        Risk score at the rejection limit                | {NEW, RENEWAL} | 9   | 5           | APPROVED      | 250
        Risk score just over the rejection limit         | {NEW, RENEWAL} | 19  | 5           | REJECTED      | 0
        Just below the senior age threshold              | NEW            | 64  | 0           | APPROVED      | 112
        At the senior age threshold                      | NEW            | 65  | 0           | APPROVED      | 221
        """)
    void evaluatesApplication(String applicantType, int age, int claimCount, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);

        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium());
    }
}
