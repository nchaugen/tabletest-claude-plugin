package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @Description("""
        The internal risk score (age/10 + claims x a weight) is never exposed, so the decision
        and the premium are verified together per row rather than split into a classification
        table and an arithmetic table; an accessor for the score would allow two tables.
        Auto-approval requires zero claims, which keeps the risk score low enough that it never
        collides with the rejection threshold in practice, so that precedence pair is not shown.
        """)
    @TableTest("""
        Scenario                                 | Applicant Type | Age | Claim Count | Decision?     | Premium?
        Renewal with no claims                   | RENEWAL        | 40  | 0           | AUTO_APPROVED | 0.0
        Renewal with one claim                   | RENEWAL        | 40  | 1           | APPROVED      | 138.0
        New application with no claims           | NEW            | 40  | 0           | APPROVED      | 108.0
        Renewal with no claims, senior applicant | RENEWAL        | 70  | 0           | AUTO_APPROVED | 0.0
        High-risk senior applicant               | NEW            | 70  | 5           | REJECTED      | 0.0
        Risk score at the rejection limit        | NEW            | 9   | 5           | APPROVED      | 250.0
        Risk score just past the rejection limit | NEW            | 10  | 5           | REJECTED      | 0.0
        Just below the senior pricing threshold  | NEW            | 64  | 0           | APPROVED      | 112.0
        At the senior pricing threshold          | NEW            | 65  | 0           | APPROVED      | 221.0
        """)
    void decidesTheOutcomeAndPremiumForAnApplication(
            String applicantType, int age, int claimCount, String decision, double premium) {
        assertEquals(new EvaluationResult(decision, premium),
                evaluator.evaluateApplication(applicantType, age, claimCount));
    }
}
