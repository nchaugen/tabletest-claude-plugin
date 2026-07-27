package com.example.loan;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @DisplayName("Applies an age-adjusted credit score threshold")
    @Description("""
        Stable income is fixed to true for every row so only the threshold
        boundary is exercised. Income effects are covered in a separate table.
        """)
    @TableTest("""
        Scenario                               | Customer Age | Credit Score | Result?
        At the standard threshold              | 40           | 650          | REJECTED
        Just above the standard threshold      | 40           | 651          | APPROVED
        Just below 65, in the senior score range | 64         | 601          | REJECTED
        At 65, in the senior score range       | 65           | 601          | APPROVED
        At the senior threshold                | 65           | 600          | REJECTED
        """)
    void appliesAgeAdjustedCreditScoreThreshold(int customerAge, int creditScore, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(customerAge, creditScore, true));
    }

    @DisplayName("Decides approval from stable income once the score clears its threshold")
    @Description("""
        Credit score is fixed at 700, comfortably above both the standard (650)
        and senior (600) thresholds. Customer age varies over a value set to
        show the income decision applies the same way to standard and senior
        applicants. Assumption: known-unstable income (false) is rejected,
        the same as a below-threshold score - only missing income (null)
        triggers PENDING_REVIEW.
        """)
    @TableTest("""
        Scenario         | Customer Age | Stable Income | Result?
        Stable income    | {30, 70}     | true          | APPROVED
        Unstable income  | {30, 70}     | false         | REJECTED
        Missing income   | {30, 70}     |               | PENDING_REVIEW
        """)
    void decidesFromStableIncomeAboveThreshold(int customerAge, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(customerAge, 700, hasStableIncome));
    }

    @DisplayName("Rejects a below-threshold score even when income is missing")
    @Description("""
        Shows the precedence between two rules: a below-threshold score is
        rejected outright, ahead of the missing-income review rule. Customer
        age is fixed at 40 (standard threshold of 650) and credit score is
        fixed at 600, below that threshold.
        """)
    @TableTest("""
        Scenario                                       | Stable Income | Result?
        Below threshold, regardless of stable income    | {true, false} | REJECTED
        Below threshold, even with missing income       |               | REJECTED
        """)
    void rejectsBelowThresholdRegardlessOfIncome(Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(40, 600, hasStableIncome));
    }
}
