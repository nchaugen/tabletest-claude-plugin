package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @DisplayName("Routes an application to auto-approval, rejection, or standard evaluation")
    @Description("""
        Auto-approval requires both a RENEWAL applicant type and zero prior claims;
        either condition alone falls through to the risk check. Risk Score is left
        blank where the decision is reached before risk is evaluated. Age is chosen
        to land exactly on the risk-score boundary (75/76) in the threshold rows and
        is not meant to represent a realistic applicant age there.
        """)
    @TableTest("""
        Scenario                          | Applicant Type | Age            | Claim Count | Risk Score? | Decision?     | Premium?
        Renewal with no claims history    | RENEWAL        | {25, 70, 900}  | 0           |             | AUTO_APPROVED | 0
        Renewal with a poor claims record | RENEWAL        | 30             | 6           | 93          | REJECTED      | 0
        New applicant, no claims          | NEW             | 40            | 0           | 4           | APPROVED      | 108.0
        At the risk limit                 | NEW             | 9             | 5           | 75          | APPROVED      | 250.0
        Just past the risk limit          | NEW             | 10            | 5           | 76          | REJECTED      | 0
        """)
    void routesApplicationToDecision(String applicantType, int age, int claimCount,
                                      Integer riskScore, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(new EvaluationResult(decision, premium), result);
    }

    @DisplayName("Prices an approved application by age tier")
    @Description("""
        Covers only the APPROVED path (auto-approval and rejection are covered above).
        Applicant Type is fixed to NEW so no row is auto-approved. Below age 65 the
        standard rate applies; from age 65 the senior rate applies.
        """)
    @TableTest("""
        Scenario                        | Age | Claim Count | Risk Score? | Premium?
        Just below the senior threshold | 64  | 0           | 6           | 112.0
        At the senior threshold         | 65  | 0           | 6           | 221.0
        Standard tier with claims       | 40  | 2           | 34          | 168.0
        Senior tier with claims         | 80  | 1           | 23          | 280.5
        """)
    void pricesApprovedApplication(int age, int claimCount, int riskScore, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, claimCount);
        assertEquals(new EvaluationResult("APPROVED", premium), result);
    }
}
