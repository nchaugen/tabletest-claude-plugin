package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @DisplayName("Auto-approves renewal applicants with no claims")
    @Description("""
        Auto-approval is checked before the risk score, so a renewal applicant with no
        claims is approved even when age alone would push the risk score past the
        rejection threshold; the second row uses an unrealistic age purely to show that
        ordering.
        """)
    @TableTest("""
        Scenario                                                   | Applicant Type | Age | Claim Count | Decision?     | Premium?
        Renewal, no claims                                         | RENEWAL        | 30  | 0           | AUTO_APPROVED | 0
        Renewal, no claims, age alone exceeds the reject threshold | RENEWAL        | 760 | 0           | AUTO_APPROVED | 0
        Renewal, one claim                                         | RENEWAL        | 30  | 1           | APPROVED      | 136
        New applicant, no claims                                   | NEW            | 30  | 0           | APPROVED      | 106
        """)
    void autoApprovesRenewalApplicantsWithNoClaims(
            String applicantType, int age, int claimCount, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium());
    }

    @DisplayName("Rejects applicants whose risk score exceeds the threshold")
    @Description("""
        The risk score is computed internally from age and claim count and is not exposed
        by the API, so the boundary is reached by varying age at a fixed claim count
        rather than by a risk-score column. Applicant type is varied across both values
        that never trigger auto-approval here, to show it plays no part in this decision.
        Rejection Threshold documents the internal constant the risk score is compared
        against; it is not an argument to the call.
        """)
    @TableTest("""
        Scenario                                     | Applicant Type | Age | Claim Count | Rejection Threshold (Policy) | Decision? | Premium?
        Risk score at the rejection threshold        | {RENEWAL, NEW} | 9   | 5           | 75                           | APPROVED  | 250
        Risk score just past the rejection threshold | {RENEWAL, NEW} | 10  | 5           | 75                           | REJECTED  | 0
        """)
    void rejectsApplicantsWhoseRiskScoreExceedsTheThreshold(
            String applicantType, int age, int claimCount, int rejectionThreshold, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium());
    }

    @DisplayName("Computes premium for approved applicants by age band")
    @Description("""
        Age 64 and 65 carry the same age-derived contribution to the internal risk score,
        isolating the formula switch at the senior threshold from any change in risk
        score.
        """)
    @TableTest("""
        Scenario                            | Applicant Type | Age | Claim Count | Decision? | Premium?
        Age just below the senior threshold | NEW            | 64  | 0           | APPROVED  | 112
        Age at the senior threshold         | NEW            | 65  | 0           | APPROVED  | 221
        Standard-rate applicant with claims | NEW            | 30  | 2           | APPROVED  | 166
        Senior applicant with claims        | NEW            | 70  | 1           | APPROVED  | 277
        """)
    void computesPremiumForApprovedApplicantsByAgeBand(
            String applicantType, int age, int claimCount, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium());
    }

    @DisplayName("Throws a NullPointerException for a null applicant type")
    @TableTest("""
        Scenario                | Applicant Type | Age | Claim Count | Throws?
        Null applicant type     |                | 30  | 0           | java.lang.NullPointerException
        Non-null applicant type | NEW            | 30  | 0           |
        """)
    void throwsForANullApplicantType(
            String applicantType, int age, int claimCount, Class<? extends Throwable> throws_) {
        assertEquals(throws_, thrownBy(() -> evaluator.evaluateApplication(applicantType, age, claimCount)));
    }

    private static Class<? extends Throwable> thrownBy(Executable action) {
        try {
            action.execute();
            return null;
        } catch (Throwable thrown) {
            return thrown.getClass();
        }
    }
}
