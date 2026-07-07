package com.example.loan;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @Description("""
        Standard applicants need a credit score above 650; applicants aged 65 or older
        need a score above 600. Assumes that when the score meets the age-appropriate
        threshold but stable income is confirmed false, the loan is rejected (the spec
        states approval requires both conditions but does not name the outcome when
        income is confirmed unstable).
        """)
    @TableTest("""
        Scenario                                                        | Age | Credit Score | Stable Income | Result?
        Standard applicant, above threshold, stable income              | 40  | 651          | true           | APPROVED
        Standard applicant, at threshold, not above it                  | 40  | 650          | true           | REJECTED
        Standard applicant, above threshold, unstable income            | 40  | 651          | false          | REJECTED
        Standard applicant, below threshold, regardless of income       | 40  | 500          | {true, false}  | REJECTED
        Just below senior age, standard threshold still applies         | 64  | 601          | true           | REJECTED
        Senior applicant, above lower threshold, stable income          | 65  | 601          | true           | APPROVED
        Senior applicant, at lower threshold, not above it              | 65  | 600          | true           | REJECTED
        Senior applicant, above lower threshold, unstable income        | 65  | 601          | false          | REJECTED
        Senior applicant, below lower threshold, regardless of income   | 65  | 400          | {true, false}  | REJECTED
        Senior applicant, well above lower threshold                    | 80  | 700          | true           | APPROVED
        """)
    void evaluatesApprovalByAgeAdjustedThresholdAndIncome(int age, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, hasStableIncome));
    }

    @Description("""
        Assumes missing income information (null) only produces PENDING_REVIEW when the
        credit score meets the age-appropriate threshold. Below-threshold scores are
        rejected regardless of income, and that rule takes precedence over the missing-income
        rule even when income is unknown.
        """)
    @TableTest("""
        Scenario                                                  | Age | Credit Score | Stable Income | Result?
        Standard applicant, above threshold, income unknown       | 40  | 651          |                | PENDING_REVIEW
        Standard applicant, at threshold, income unknown          | 40  | 650          |                | REJECTED
        Standard applicant, below threshold, income unknown       | 40  | 500          |                | REJECTED
        Senior applicant, above lower threshold, income unknown   | 65  | 601          |                | PENDING_REVIEW
        Senior applicant, at lower threshold, income unknown      | 65  | 600          |                | REJECTED
        Senior applicant, below lower threshold, income unknown   | 65  | 400          |                | REJECTED
        """)
    void handlesUnknownIncomeInformation(int age, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, hasStableIncome));
    }
}
