package com.example.loan;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @Description("""
        Decision precedence: the credit score threshold is checked first (600 for applicants
        65+, 650 otherwise). A below-threshold score is rejected outright. Only once the score
        clears the threshold does the income status determine the outcome.
        Assumptions (not stated explicitly in the feature description):
        - Both thresholds are exclusive lower bounds: the score must be strictly greater than
          the threshold to qualify (matches "above 650" for standard applicants).
        - Senior status applies starting at age 65 (inclusive).
        - "Below-threshold scores are rejected regardless of income" is read to include unknown
          (null) income too - a below-threshold score is rejected outright without needing to
          consult income, and never produces PENDING_REVIEW.
        - Known unstable income (hasStableIncome = false) at or above the threshold results in
          REJECTED, not PENDING_REVIEW - PENDING_REVIEW is reserved for genuinely unknown income.
        """)
    @TableTest("""
        Scenario                                              | Age | Credit Score | Has Stable Income | Result?
        Standard applicant above threshold, stable income     | 40  | 651          | true               | APPROVED
        Standard applicant at threshold, not exceeded          | 40  | 650          | true               | REJECTED
        Just below senior cutoff, standard threshold applies   | 64  | 601          | true               | REJECTED
        At senior cutoff, lower threshold applies              | 65  | 601          | true               | APPROVED
        Senior applicant at own threshold, not exceeded        | 65  | 600          | true               | REJECTED
        Standard applicant with unstable income                | 40  | 700          | false              | REJECTED
        Senior applicant with unstable income                  | 70  | 620          | false              | REJECTED
        Below standard threshold, rejected regardless of income | 40  | 500          | {true, false}      | REJECTED
        Below senior threshold, rejected regardless of income  | 70  | 550          | {true, false}      | REJECTED
        Missing income with qualifying standard score          | 40  | 700          |                    | PENDING_REVIEW
        Missing income with qualifying senior score            | 70  | 620          |                    | PENDING_REVIEW
        Missing income with below-threshold score              | 40  | 500          |                    | REJECTED
        """)
    void shouldEvaluateLoan(int age, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, hasStableIncome));
    }
}
