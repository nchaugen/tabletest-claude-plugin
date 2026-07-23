package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @Description("""
        This check runs before the risk-based rejection and premium-tier rules
        below, so a qualifying renewal is approved even at ages that would
        otherwise receive a senior-rate premium.
        """)
    @TableTest("""
        Scenario                                   | Applicant Type | Age                | Claim Count | Decision?     | Premium?
        Renewal with no claims, regardless of age  | RENEWAL        | {20, 64, 65, 90}   | 0           | AUTO_APPROVED | 0.00
        Renewal with a claim does not auto-approve | RENEWAL        | 40                 | 1           | APPROVED      | 138.00
        New applicant does not auto-approve         | NEW            | 40                 | 0           | APPROVED      | 108.00
        """)
    void autoApprovesRenewalsWithNoClaimsRegardlessOfAge(String applicantType, int age, int claimCount,
                                                          String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }

    @Description("""
        Risk score = (age / 10) + (claimCount * 15); the application is rejected
        once this score exceeds 75. The score itself is an internal calculation,
        not part of the public API, so these rows use age/claimCount combinations
        that land exactly on the threshold and just past it.
        """)
    @TableTest("""
        Scenario                             | Applicant Type  | Age | Claim Count | Decision? | Premium?
        At the risk threshold, still approved | {NEW, RENEWAL} | 0   | 5           | APPROVED  | 250.00
        Just over the risk threshold          | {NEW, RENEWAL} | 10  | 5           | REJECTED  | 0.00
        """)
    void rejectsApplicationsAboveRiskThreshold(String applicantType, int age, int claimCount,
                                                String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }

    @Description("""
        Standard tier: premium = 100 + riskScore * 2.0.
        Senior tier (age >= 65): premium = 200 + riskScore * 3.5.
        Ages 64 and 65 below produce the same risk score, isolating the tier
        boundary from any change in risk score.
        """)
    @TableTest("""
        Scenario                                  | Applicant Type  | Age | Claim Count | Decision? | Premium?
        Standard premium below senior age         | NEW             | 40  | 0           | APPROVED  | 108.00
        Standard premium just below senior age     | NEW             | 64  | 0           | APPROVED  | 112.00
        Senior premium at the boundary, same risk  | NEW             | 65  | 0           | APPROVED  | 221.00
        Senior premium with claims, any applicant  | {NEW, RENEWAL}  | 70  | 1           | APPROVED  | 277.00
        """)
    void calculatesPremiumBySeniorAgeTier(String applicantType, int age, int claimCount,
                                          String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }
}
