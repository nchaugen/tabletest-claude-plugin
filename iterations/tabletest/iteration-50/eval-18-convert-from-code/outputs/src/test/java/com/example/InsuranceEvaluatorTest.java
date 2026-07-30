package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @DisplayName("Auto-approves renewals with no claims ahead of the risk rejection")
    @Description("""
        Auto-approval is checked before the risk-score rejection, so it overrides
        a risk score that would otherwise be rejected. Both conditions - RENEWAL
        type and zero claims - are required; either one failing falls through to
        the normal risk-based evaluation.
        """)
    @TableTest("""
        Scenario                                     | Applicant Type | Age | Claim Count | Decision?     | Premium?
        Renewal with no claims                        | RENEWAL        | 30  | 0           | AUTO_APPROVED | 0
        Renewal with no claims, age alone risks reject | RENEWAL        | 999 | 0           | AUTO_APPROVED | 0
        Renewal with a claim                           | RENEWAL        | 30  | 1           | APPROVED      | 136.0
        New applicant with no claims                   | NEW            | 40  | 0           | APPROVED      | 108.0
        """)
    void autoApprovesRenewalsWithNoClaims(String applicantType, int age, int claimCount, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);

        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.0001);
    }

    @DisplayName("Rejects applications whose risk score exceeds the policy threshold")
    @Description("""
        Applicant Type is held at NEW throughout so the renewal auto-approval
        shortcut (see autoApprovesRenewalsWithNoClaims) does not interfere with
        this threshold check.
        """)
    @TableTest("""
        Scenario                  | Age | Claim Count | Risk Threshold | Decision? | Premium?
        At the risk threshold     | 9   | 5           | 75             | APPROVED  | 250.0
        Just above the risk threshold | 10 | 5         | 75             | REJECTED  | 0
        """)
    void rejectsApplicationsAboveRiskThreshold(int age, int claimCount, int riskThreshold, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, claimCount);

        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.0001);
    }

    @DisplayName("Switches to the senior premium formula at the senior age threshold")
    @Description("""
        Applicant Type is held at NEW and Claim Count at 1 throughout, isolating
        the age threshold that switches the premium formula from the risk-score
        arithmetic covered separately below.
        """)
    @TableTest("""
        Scenario                | Age | Senior Age Threshold | Premium?
        Just below the senior age | 64  | 65                    | 142.0
        At the senior age         | 65  | 65                    | 273.5
        """)
    void selectsPremiumFormulaByAge(int age, int seniorAgeThreshold, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, 1);

        assertEquals(premium, result.premium(), 0.0001);
    }

    @DisplayName("Computes the standard premium from risk score below the senior age")
    @Description("""
        Age is held at 30 (below the senior threshold) and Applicant Type at NEW
        throughout, isolating how risk score drives the standard premium.
        """)
    @TableTest("""
        Scenario           | Claim Count | Premium?
        No prior claims    | 0           | 106.0
        Two prior claims   | 2           | 166.0
        Four prior claims  | 4           | 226.0
        """)
    void computesStandardPremiumFromRiskScore(int claimCount, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", 30, claimCount);

        assertEquals(premium, result.premium(), 0.0001);
    }

    @DisplayName("Computes the senior premium from risk score at or above the senior age")
    @Description("""
        Age is held at 65 (at the senior threshold) and Applicant Type at NEW
        throughout, isolating how risk score drives the senior premium.
        """)
    @TableTest("""
        Scenario           | Claim Count | Premium?
        No prior claims    | 0           | 221.0
        Two prior claims   | 2           | 326.0
        Four prior claims  | 4           | 431.0
        """)
    void computesSeniorPremiumFromRiskScore(int claimCount, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", 65, claimCount);

        assertEquals(premium, result.premium(), 0.0001);
    }
}
