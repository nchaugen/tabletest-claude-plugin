package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @TableTest("""
        Scenario                                            | Applicant Type | Claim Count | Age            | Decision?     | Premium?
        Renewal with no claims auto-approves regardless of age | RENEWAL     | 0           | {18, 45, 70}   | AUTO_APPROVED | 0.0
        Renewal with a claim falls back to risk evaluation  | RENEWAL        | 1           | 30             | APPROVED      | 136.0
        New applicant with no claims falls back to risk evaluation | NEW     | 0           | 30             | APPROVED      | 106.0
        """)
    void autoApprovesRenewalsWithZeroClaimsRegardlessOfAge(String applicantType, int claimCount, int age,
                                                            String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium());
    }

    @Description("""
        Internal risk score is age/10 + claimCount*15 (not exposed by evaluateApplication).
        Applicant type is fixed at NEW and claim count at 5 to keep the renewal
        auto-approval rule out of scope. Age is varied across the /10 boundary
        (9 to 10) to land exactly on the risk score threshold of 75.
        """)
    @TableTest("""
        Scenario                     | Age | Claim Count | Max Risk Score | Decision? | Premium?
        At the risk threshold        | 9   | 5           | 75             | APPROVED  | 250.0
        Just above the risk threshold| 10  | 5           | 75             | REJECTED  | 0.0
        """)
    void rejectsApplicationsAboveRiskThreshold(int age, int claimCount, int maxRiskScore,
                                                String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium());
    }

    @Description("""
        Applicant type is fixed at NEW throughout, isolating premium arithmetic
        from the renewal auto-approval rule. Internal risk score is
        age/10 + claimCount*15. Holding risk score constant at 6 across the
        age-65 boundary shows the tier switch; varying claim count within a
        tier shows the per-tier rate: standard premium is 100 + riskScore*2.0,
        senior premium is 200 + riskScore*3.5.
        """)
    @TableTest("""
        Scenario                                  | Age | Claim Count | Premium?
        Standard tier at the age boundary          | 64  | 0           | 112.0
        Senior tier at the age boundary             | 65  | 0           | 221.0
        Standard tier, higher risk score from claims| 30  | 2           | 166.0
        Senior tier, higher risk score from claims  | 70  | 2           | 329.5
        """)
    void calculatesPremiumByAgeTierAndRiskScore(int age, int claimCount, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, claimCount);
        assertEquals("APPROVED", result.decision());
        assertEquals(premium, result.premium());
    }
}
