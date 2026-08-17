package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @DisplayName("Auto-approves a renewal applicant with no claims")
    @Description("""
        Auto-approval requires both conditions together: applicant type RENEWAL and zero claims.
        Age is held at 40 throughout since it plays no part in this rule; where auto-approval does
        not fire, age 40 keeps the risk score low enough to fall through to a plain APPROVED
        decision rather than a REJECTED one, so each row shows only what this rule decides.
        """)
    @TableTest("""
        Scenario                       | Applicant Type | Age | Claim Count | Decision?     | Premium?
        Renewal with no claims         | RENEWAL        | 40  | 0           | AUTO_APPROVED | 0
        Renewal with a claim on record | RENEWAL        | 40  | 1           | APPROVED      | 138
        New applicant with no claims   | NEW            | 40  | 0           | APPROVED      | 108
        """)
    void autoApprovesARenewalApplicantWithNoClaims(
            String applicantType, int age, int claimCount, String decision, double premium) {
        assertEquals(new EvaluationResult(decision, premium),
                evaluator.evaluateApplication(applicantType, age, claimCount));
    }

    @DisplayName("Prioritizes auto-approval over risk-based rejection")
    @Description("""
        Age 760 alone drives the internal risk score past the rejection threshold. These rows
        show that a renewal with no claims is still auto-approved despite that, while the same
        high-risk profile with one claim on record is rejected as usual.
        """)
    @TableTest("""
        Scenario                                             | Applicant Type | Age | Claim Count | Decision?     | Premium?
        High-risk renewal with no claims still auto-approves | RENEWAL        | 760 | 0           | AUTO_APPROVED | 0
        Same high-risk profile with a claim is rejected      | RENEWAL        | 760 | 1           | REJECTED      | 0
        """)
    void prioritizesAutoApprovalOverRiskBasedRejection(
            String applicantType, int age, int claimCount, String decision, double premium) {
        assertEquals(new EvaluationResult(decision, premium),
                evaluator.evaluateApplication(applicantType, age, claimCount));
    }

    @DisplayName("Rejects an application whose risk score exceeds the threshold")
    @Description("""
        The risk score is computed internally from age and claim count and is not exposed by the
        public API, so decision and premium are verified together here. Age and Claim Count are
        chosen so the internal score lands exactly at, then one point past, the rejection
        threshold. Applicant type is a non-renewal value throughout so the auto-approval rule
        never interferes.
        """)
    @TableTest("""
        Scenario                          | Applicant Type | Age | Claim Count | Rejection Threshold (Risk Score) | Decision? | Premium?
        At the rejection threshold        | NEW            | 5   | 5           | 75                               | APPROVED  | 250
        Just past the rejection threshold | NEW            | 10  | 5           | 75                               | REJECTED  | 0
        """)
    void rejectsAnApplicationWhoseRiskScoreExceedsTheThreshold(
            String applicantType, int age, int claimCount, int rejectionThreshold, String decision, double premium) {
        assertEquals(new EvaluationResult(decision, premium),
                evaluator.evaluateApplication(applicantType, age, claimCount));
    }

    @DisplayName("Selects the premium formula by the applicant's age")
    @Description("""
        Applicant type is held at a non-renewal value throughout; the renewal/zero-claims
        interaction is covered separately above. Premium is a linear function of the internal
        risk score, which is not exposed by the public API, so decision and premium are verified
        together here.
        """)
    @TableTest("""
        Scenario                           | Applicant Type | Age | Senior Age Threshold | Claim Count | Decision? | Premium?
        Just below the senior threshold    | NEW            | 64  | 65                   | 0           | APPROVED  | 112
        At the senior threshold            | NEW            | 65  | 65                   | 0           | APPROVED  | 221
        Standard premium rises with claims | NEW            | 30  | 65                   | 2           | APPROVED  | 166
        Senior premium rises with claims   | NEW            | 70  | 65                   | 2           | APPROVED  | 329.5
        """)
    void selectsThePremiumFormulaByTheApplicantsAge(
            String applicantType, int age, int seniorAgeThreshold, int claimCount, String decision, double premium) {
        assertEquals(new EvaluationResult(decision, premium),
                evaluator.evaluateApplication(applicantType, age, claimCount));
    }
}
