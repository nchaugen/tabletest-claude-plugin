package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @DisplayName("Auto-approves a renewal application with no prior claims, ahead of risk and age checks")
    @Description("""
        Bypasses risk-score rejection and the senior premium rule even when either would otherwise
        apply, including an implausibly high age used only to force what would be a rejecting risk
        score.
        """)
    @TableTest("""
        Scenario                                  | Applicant Type | Age | Claim Count | Decision?     | Premium?
        Zero-claim renewal at an ordinary age     | RENEWAL        | 40  | 0           | AUTO_APPROVED | 0
        Zero-claim renewal at a senior age        | RENEWAL        | 70  | 0           | AUTO_APPROVED | 0
        Zero-claim renewal despite an extreme age | RENEWAL        | 800 | 0           | AUTO_APPROVED | 0
        Renewal with a prior claim                | RENEWAL        | 40  | 1           | APPROVED      | 138.0
        Non-renewal applicant with no claims      | NEW            | 40  | 0           | APPROVED      | 108.0
        """)
    void bypassesEvaluationForZeroClaimRenewals(
            String applicantType, int age, int claimCount, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }

    @DisplayName("Decides the outcome and premium from risk score and age once the renewal bypass does not apply")
    @Description("""
        The risk score combines age and claim count internally and is never exposed, so it is not a
        column here; expected decisions and premiums are stated directly. Applicant type has no
        further effect once the renewal bypass above does not apply.
        """)
    @TableTest("""
        Scenario                                    | Applicant Type | Age | Claim Count | Decision? | Premium?
        Claim on file, regardless of applicant type | {RENEWAL, NEW} | 30  | 2           | APPROVED  | 166.0
        At the risk-score rejection limit           | NEW            | 9   | 5           | APPROVED  | 250.0
        Just past the risk-score rejection limit    | NEW            | 19  | 5           | REJECTED  | 0
        Just below the senior age threshold         | NEW            | 64  | 0           | APPROVED  | 112.0
        At the senior age threshold                 | NEW            | 65  | 0           | APPROVED  | 221.0
        Senior applicant with a higher risk score   | NEW            | 70  | 3           | APPROVED  | 382.0
        """)
    void decidesOutcomeAndPremiumFromRiskAndAge(
            String applicantType, int age, int claimCount, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }
}
