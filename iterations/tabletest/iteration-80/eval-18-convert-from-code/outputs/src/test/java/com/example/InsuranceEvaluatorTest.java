package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @Description("""
        Auto-approval is checked before the risk rejection and premium-tier rules, so it
        overrides both: a renewal with no claims is auto-approved even at an age whose risk
        score would otherwise be rejected, and even at an age that would otherwise use the
        senior premium formula. No age validation exists, so an implausible age is used
        deliberately to isolate this precedence.
        """)
    @TableTest("""
        Scenario                              | Applicant Type  | Age | Claim Count | Decision?     | Premium?
        Renewal with no claims                | RENEWAL         | 30  | 0           | AUTO_APPROVED | 0
        Renewal with no claims, senior age    | RENEWAL         | 70  | 0           | AUTO_APPROVED | 0
        Renewal with no claims, high-risk age | RENEWAL         | 800 | 0           | AUTO_APPROVED | 0
        Renewal with a claim                  | RENEWAL         | 30  | 1           | APPROVED      | 136.0
        Non-renewal applicant type            | {NEW, PROSPECT} | 30  | 0           | APPROVED      | 106.0
        """)
    void grantsAutomaticApprovalForClaimFreeRenewals(String applicantType, int age, int claimCount,
                                                       String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);

        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.0001);
    }

    @Description("""
        The risk score is internal and not exposed by the public API, so age and claim count
        are chosen to land exactly either side of the rejection threshold and the decision and
        premium are verified together from those raw inputs. Applicant type does not affect
        this rule once claim count is above zero, since that already rules out auto-approval.
        """)
    @TableTest("""
        Scenario                     | Applicant Type | Age | Claim Count | Decision? | Premium?
        At the risk threshold        | {NEW, RENEWAL} | 9   | 5           | APPROVED  | 250.0
        Just past the risk threshold | {NEW, RENEWAL} | 10  | 5           | REJECTED  | 0
        """)
    void rejectsApplicationsAboveTheRiskThreshold(String applicantType, int age, int claimCount,
                                                   String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);

        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.0001);
    }

    @Description("""
        Applies once an application is neither auto-approved nor rejected. Applicant type is
        held at NEW throughout, since the other tables already establish that it does not
        affect this rule.
        """)
    @TableTest("""
        Scenario                    | Age | Claim Count | Premium?
        Minimal risk, standard tier | 20  | 0           | 104.0
        Highest standard-tier age   | 64  | 0           | 112.0
        First senior-tier age       | 65  | 0           | 221.0
        Higher risk, senior tier    | 80  | 2           | 333.0
        """)
    void calculatesPremiumByAgeTier(int age, int claimCount, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, claimCount);

        assertEquals(premium, result.premium(), 0.0001);
    }
}
