package com.example.loan;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @Description("""
        Applicants aged 65+ use a lower credit score threshold (600) than
        standard applicants (650). Credit score is fixed at 620 - above the
        senior threshold but at/below the standard one - and income is held
        stable, so only the age-driven threshold shift affects the outcome.
        """)
    @TableTest("""
        Scenario                 | Age | Credit Score | Stable Income | Approval?
        Just under senior age    | 64  | 620          | true          | REJECTED
        At senior age boundary   | 65  | 620          | true          | APPROVED
        """)
    void appliesLowerThresholdForSeniorApplicants(int age, int creditScore, Boolean hasStableIncome, ApprovalResult approval) {
        assertEquals(approval, evaluator.evaluateLoan(age, creditScore, hasStableIncome));
    }

    @Description("""
        Standard (non-senior) applicants are approved only above a credit
        score of 650 AND with stable income. Age is fixed at 40, a
        representative non-senior age - see appliesLowerThresholdForSeniorApplicants
        for the age boundary itself.
        """)
    @TableTest("""
        Scenario                          | Credit Score | Stable Income | Approval?
        Above threshold, stable income     | 651          | true          | APPROVED
        Above threshold, unstable income   | 651          | false         | REJECTED
        At threshold                       | 650          | {true, false} | REJECTED
        Below threshold                    | 600          | {true, false} | REJECTED
        """)
    void approvesStandardApplicantsAboveThreshold(int creditScore, Boolean hasStableIncome, ApprovalResult approval) {
        assertEquals(approval, evaluator.evaluateLoan(40, creditScore, hasStableIncome));
    }

    @Description("""
        Senior applicants (65+) are approved above the lower credit score
        threshold of 600 AND with stable income. Age is fixed at 70, a
        representative senior age.
        """)
    @TableTest("""
        Scenario                          | Credit Score | Stable Income | Approval?
        Above threshold, stable income     | 601          | true          | APPROVED
        Above threshold, unstable income   | 601          | false         | REJECTED
        At threshold                       | 600          | {true, false} | REJECTED
        Below threshold                    | 550          | {true, false} | REJECTED
        """)
    void approvesSeniorApplicantsAboveLowerThreshold(int creditScore, Boolean hasStableIncome, ApprovalResult approval) {
        assertEquals(approval, evaluator.evaluateLoan(70, creditScore, hasStableIncome));
    }

    @Description("""
        Missing income information (blank cell = null) always yields
        PENDING_REVIEW, regardless of age or credit score - including scores
        that would otherwise be rejected. Assumed precedence: the null-income
        check is evaluated before any threshold comparison.
        """)
    @TableTest("""
        Scenario                             | Age        | Credit Score | Stable Income | Approval?
        Missing income, regardless of age/score | {30, 70} | {500, 700}   |               | PENDING_REVIEW
        """)
    void flagsMissingIncomeAsPendingReview(int age, int creditScore, Boolean hasStableIncome, ApprovalResult approval) {
        assertEquals(approval, evaluator.evaluateLoan(age, creditScore, hasStableIncome));
    }
}
