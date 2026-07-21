package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @Description("""
        A renewal applicant with zero claims is auto-approved before the risk
        score or age is even considered. Rows 1-2 show this overriding the
        outcome the applicant would otherwise get (standard or senior approval);
        row 3 shows a claim breaks the bypass; row 4 shows a non-renewal
        applicant with zero claims does not qualify either.
        """)
    @TableTest("""
        Scenario                                        | Applicant Type | Age | Claim Count | Status?       | Premium?
        Renewal, no claims, would otherwise be standard | RENEWAL        | 30  | 0           | AUTO_APPROVED | 0.00
        Renewal, no claims, would otherwise be senior    | RENEWAL        | 70  | 0           | AUTO_APPROVED | 0.00
        Renewal with a claim                             | RENEWAL        | 30  | 1           | APPROVED      | 136.00
        New applicant, no claims                          | NEW            | 30  | 0           | APPROVED      | 106.00
        """)
    void autoApprovesRenewalsWithNoClaims(String applicantType, int age, int claimCount,
                                           String status, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(status, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }

    @Description("""
        Risk score is age/10 + claimCount * 15. Applications are rejected once
        the score exceeds 75. Row 1 confirms applicant type does not affect
        this decision. Rows 2-3 straddle the threshold: age 9 (age/10 == 0)
        with 5 claims lands exactly on 75 and is still approved; age 10 with
        5 claims crosses to 76 and is rejected.
        """)
    @TableTest("""
        Scenario                             | Applicant Type | Age | Claim Count | Status?  | Premium?
        Well below threshold, any applicant  | {NEW, RENEWAL} | 20  | 1           | APPROVED | 134.00
        Risk score exactly at threshold      | NEW            | 9   | 5           | APPROVED | 250.00
        Risk score just above threshold      | NEW            | 10  | 5           | REJECTED | 0.00
        Well above threshold                 | NEW            | 50  | 10          | REJECTED | 0.00
        """)
    void rejectsApplicationsAboveRiskThreshold(String applicantType, int age, int claimCount,
                                                String status, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(status, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }

    @Description("""
        Approved applicants are charged 100 + riskScore * 2.0 below age 65,
        and 200 + riskScore * 3.5 from age 65 onward. Rows 1-2 hold claim
        count fixed at the age-65 boundary to isolate the tier switch; rows
        3-4 vary age further with no claims. Applicant type does not affect
        premium (see the auto-approval and rejection tables for its effects),
        so it is held fixed at NEW here.
        """)
    @TableTest("""
        Scenario                          | Age | Claim Count | Premium?
        Just below senior threshold (64)  | 64  | 1           | 142.00
        At senior threshold (65)          | 65  | 1           | 273.50
        Well below threshold, no claims   | 30  | 0           | 106.00
        Well above threshold, no claims   | 90  | 0           | 231.50
        """)
    void calculatesPremiumByAgeTier(int age, int claimCount, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, claimCount);
        assertEquals("APPROVED", result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }
}
