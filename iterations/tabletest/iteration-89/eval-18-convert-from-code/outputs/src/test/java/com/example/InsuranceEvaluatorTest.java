package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @DisplayName("Auto-approves renewal applicants with no prior claims")
    @Description("""
        Renewal + zero claims takes priority over every other rule, including age-based
        pricing: Age is varied across an ordinary and a senior-qualifying value to show the
        auto-approval holds regardless of age. Applicant Type and Claim Count are varied
        independently to show both conditions are required together.
        """)
    @TableTest("""
        Scenario                        | Applicant Type | Age      | Claim Count | Decision?     | Premium?
        Renewal with no claims, any age | RENEWAL        | {30, 70} | 0           | AUTO_APPROVED | 0
        Renewal with a prior claim      | RENEWAL        | 30       | 1           | APPROVED      | 136
        New applicant with no claims    | NEW            | 30       | 0           | APPROVED      | 106
        """)
    void autoApprovesRenewalApplicantsWithNoClaims(
            String applicantType, int age, int claimCount, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }

    @DisplayName("Rejects applications that exceed the risk threshold")
    @Description("""
        Applicant Type and Claim Count are held at values that do not qualify for automatic
        renewal approval, isolating the risk-based rejection boundary from that rule.
        """)
    @TableTest("""
        Scenario                     | Applicant Type | Age | Claim Count | Decision? | Premium?
        At the risk threshold        | NEW            | 0   | 5           | APPROVED  | 250
        Just past the risk threshold | NEW            | 10  | 5           | REJECTED  | 0
        """)
    void rejectsApplicationsExceedingTheRiskThreshold(
            String applicantType, int age, int claimCount, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }

    @DisplayName("Prices the premium by age tier for approved applications")
    @Description("""
        Applicant Type and Claim Count are held fixed at values that trigger neither the
        renewal auto-approval nor the risk-based rejection, isolating the senior age-tier
        boundary alone.
        """)
    @TableTest("""
        Scenario                            | Applicant Type | Age | Claim Count | Decision? | Premium?
        Just below the senior age threshold | NEW            | 64  | 2           | APPROVED  | 172
        At the senior age threshold         | NEW            | 65  | 2           | APPROVED  | 326
        """)
    void pricesThePremiumByAgeTier(
            String applicantType, int age, int claimCount, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }
}
