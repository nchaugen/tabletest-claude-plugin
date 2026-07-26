package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private static final double DELTA = 0.001;

    @Description("""
        A RENEWAL application with zero claims is auto-approved with a flat premium of 0,
        bypassing risk scoring and the age-based premium tiers entirely. Both conditions
        (type and claim count) must hold for the shortcut to apply.
        """)
    @TableTest("""
        Scenario                                    | Applicant Type | Age | Claim Count | Decision?      | Premium?
        Renewal with no claims bypasses risk and age | RENEWAL        | 70  | 0           | AUTO_APPROVED  | 0.00
        Renewal with a claim falls through           | RENEWAL        | 30  | 1           | APPROVED       | 136.00
        Non-renewal with no claims is not auto-approved | NEW         | 30  | 0           | APPROVED       | 106.00
        """)
    void evaluatesRenewalShortcut(String applicantType, int age, int claimCount, String decision, double premium) {
        InsuranceEvaluator evaluator = new InsuranceEvaluator();
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), DELTA);
    }

    @Description("""
        Risk score = (age / 10, integer division) + (claimCount * 15). Applications are
        rejected once the risk score exceeds 75. Ages are kept in the single digits/teens
        here so the risk score is driven almost entirely by claim count, letting a small
        age change cross the boundary from 75 to 76.
        """)
    @TableTest("""
        Scenario                    | Applicant Type | Age | Claim Count | Decision? | Premium?
        At the risk limit           | NEW            | 8   | 5           | APPROVED  | 250.00
        Just past the risk limit    | NEW            | 15  | 5           | REJECTED  | 0.00
        """)
    void evaluatesRiskRejectionThreshold(String applicantType, int age, int claimCount, String decision, double premium) {
        InsuranceEvaluator evaluator = new InsuranceEvaluator();
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), DELTA);
    }

    @Description("""
        Below age 65, premium = 100 + riskScore * 2.0. From age 65 onward (inclusive),
        premium = 200 + riskScore * 3.5. Age 64 and 65 both have the same risk score (21,
        since floor(64/10) == floor(65/10) == 6), so these two rows isolate the age-tier
        switch from any change in risk score.
        """)
    @TableTest("""
        Scenario                     | Applicant Type | Age | Claim Count | Decision? | Premium?
        Just below the senior tier   | NEW            | 64  | 1           | APPROVED  | 142.00
        At the senior tier threshold | NEW            | 65  | 1           | APPROVED  | 273.50
        """)
    void evaluatesPremiumByAgeTier(String applicantType, int age, int claimCount, String decision, double premium) {
        InsuranceEvaluator evaluator = new InsuranceEvaluator();
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), DELTA);
    }
}
