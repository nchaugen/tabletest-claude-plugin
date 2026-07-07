package com.example;

import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @TableTest("""
        Scenario                                          | Applicant Type | Age        | Claim Count | Status?       | Premium?
        Renewal with no claims auto-approves regardless of age | RENEWAL   | {25, 70}   | 0           | AUTO_APPROVED | 0.00
        Renewal with a claim is evaluated normally         | RENEWAL        | 30         | 1           | APPROVED      | 136.00
        New applicant is never auto-approved               | NEW            | 30         | 0           | APPROVED      | 106.00
        Risk score at the rejection threshold is approved  | NEW            | 0          | 5           | APPROVED      | 250.00
        Risk score just above the threshold is rejected    | NEW            | 10         | 5           | REJECTED      | 0.00
        High risk is rejected even for a senior applicant  | NEW            | 70         | 6           | REJECTED      | 0.00
        """)
    void resolvesApplicationOutcome(String applicantType, int age, int claimCount, String status, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);

        assertEquals(status, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }

    @TableTest("""
        Scenario                                    | Age | Claim Count | Premium?
        Young applicant with no claims               | 30  | 0           | 106.00
        Just below the senior age boundary           | 64  | 0           | 112.00
        At the senior age boundary                   | 65  | 0           | 221.00
        Senior applicant with a claim                | 70  | 1           | 277.00
        Standard applicant with multiple claims       | 30  | 2           | 166.00
        """)
    void calculatesPremiumForApprovedApplications(int age, int claimCount, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, claimCount);

        assertEquals("APPROVED", result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }
}
