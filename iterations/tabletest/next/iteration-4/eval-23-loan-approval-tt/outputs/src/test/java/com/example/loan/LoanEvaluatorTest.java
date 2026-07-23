package com.example.loan;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @Description("""
        Applicants with stable income (true, fixed for every row) are approved only when
        their credit score is above the applicable threshold. Standard threshold is 650;
        applicants aged 65+ get a lower threshold of 600.
        """)
    @TableTest("""
        Scenario                                      | Age | Credit Score | Result?
        Below senior age, at standard threshold       | 64  | 650          | REJECTED
        Below senior age, above standard threshold    | 64  | 651          | APPROVED
        Below senior age, above senior threshold only | 64  | 601          | REJECTED
        At senior age, at senior threshold            | 65  | 600          | REJECTED
        At senior age, above senior threshold          | 65  | 601          | APPROVED
        Well above senior age, above senior threshold | 90  | 700          | APPROVED
        """)
    void appliesLowerThresholdForSeniorApplicants(int age, int creditScore, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, true));
    }

    @Description("""
        When hasStableIncome is null, the applicant is sent to pending review
        regardless of age or credit score.
        """)
    @TableTest("""
        Scenario                                  | Age            | Credit Score      | Result?
        Missing income regardless of age or score | {18, 65, 90}   | {300, 650, 850}   | PENDING_REVIEW
        """)
    void missingIncomeInformationYieldsPendingReview(int age, int creditScore, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, null));
    }

    @Description("""
        Combines the age-based threshold with income status to produce the final decision.
        Below-threshold scores are rejected regardless of income (true or false).
        """)
    @TableTest("""
        Scenario                                                | Age | Credit Score | Has Stable Income | Result?
        Approved: standard threshold, stable income             | 40  | 700          | true              | APPROVED
        Rejected: standard threshold, no stable income          | 40  | 700          | false             | REJECTED
        Rejected: below standard threshold, regardless of income| 40  | 600          | {true, false}     | REJECTED
        Pending: missing income information                     | 40  | 700          |                   | PENDING_REVIEW
        Approved: senior threshold, stable income                | 70  | 620          | true              | APPROVED
        Rejected: below senior threshold, regardless of income   | 70  | 550          | {true, false}     | REJECTED
        """)
    void evaluatesFinalLoanDecision(int age, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, hasStableIncome));
    }
}
