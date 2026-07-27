package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @DisplayName("Auto-approval precedence for renewals with no claims")
    @Description("""
        Auto-approval requires both conditions together: applicant type RENEWAL and
        zero prior claims. This check runs before the risk and age checks, so it wins
        even when the applicant would otherwise qualify for the senior premium.
        """)
    @TableTest("""
        Scenario                                          | Applicant Type | Age | Claim Count | Decision?     | Premium?
        Renewal with no claims auto-approves               | RENEWAL        | 30  | 0           | AUTO_APPROVED | 0.00
        Auto-approval overrides senior premium eligibility | RENEWAL        | 90  | 0           | AUTO_APPROVED | 0.00
        Renewal with a prior claim does not auto-approve   | RENEWAL        | 30  | 1           | APPROVED      | 136.00
        New applicant with no claims does not auto-approve | NEW            | 30  | 0           | APPROVED      | 106.00
        """)
    void autoApprovesRenewalsWithNoClaims(String applicantType, int age, int claimCount, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);

        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium());
    }

    @DisplayName("Risk score rejection threshold")
    @Description("""
        Combined risk from age and prior claims must stay at or below 75. Exceeding it
        rejects the application, even for an applicant old enough to otherwise qualify
        for the senior premium.
        """)
    @TableTest("""
        Scenario                                        | Applicant Type | Age | Claim Count | Decision? | Premium?
        Risk score at the threshold                     | NEW            | 0   | 5           | APPROVED  | 250.00
        Risk score just past the threshold               | NEW            | 10  | 5           | REJECTED  | 0.00
        High risk overrides senior premium eligibility   | NEW            | 70  | 5           | REJECTED  | 0.00
        """)
    void rejectsWhenCombinedRiskExceedsThreshold(String applicantType, int age, int claimCount, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);

        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium());
    }

    @DisplayName("Premium calculation by age bracket")
    @Description("""
        Applies only to applicants who pass the auto-approval and risk checks. Applicant
        type does not affect the premium once approved, so it is fixed to NEW for the
        no-claims rows (RENEWAL with zero claims would instead auto-approve) and varied
        as a value set for the with-claims rows to show it does not change the result.
        """)
    @TableTest("""
        Scenario                                       | Applicant Type | Age | Claim Count | Premium?
        Just below the senior age threshold            | NEW            | 64  | 0           | 112.00
        At the senior age threshold, same risk score    | NEW            | 65  | 0           | 221.00
        Standard schedule with prior claims             | {NEW, RENEWAL} | 20  | 2           | 164.00
        Senior schedule with prior claims                | {NEW, RENEWAL} | 80  | 2           | 333.00
        """)
    void calculatesPremiumByAgeBracket(String applicantType, int age, int claimCount, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);

        assertEquals(premium, result.premium());
    }
}
