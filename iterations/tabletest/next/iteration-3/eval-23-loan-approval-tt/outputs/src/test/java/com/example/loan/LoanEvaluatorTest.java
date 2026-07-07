package com.example.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @Description("""
        Standard applicants (under 65) are approved with a credit score strictly
        above 650; senior applicants (65+) get a lower threshold, strictly above 600.
        A score at or below the applicable threshold is REJECTED regardless of income,
        including when income is unknown - the score check gates before the income
        check. Only once the score qualifies does income status decide the outcome:
        unknown (null) income yields PENDING_REVIEW, known non-stable income (false)
        yields REJECTED, and stable income (true) yields APPROVED.
        """)
    @TableTest("""
        Scenario                                                | Age | Credit Score | Stable Income | Result?
        Standard age, well above threshold, stable income       | 30  | 700          | true          | APPROVED
        Standard age, at threshold boundary                     | 30  | 650          | true          | REJECTED
        Standard age, just above threshold                      | 30  | 651          | true          | APPROVED
        Standard age, below threshold                           | 30  | 600          | {true, false} | REJECTED
        Standard age, below threshold, income unknown           | 30  | 600          |               | REJECTED
        Standard age, above threshold, income unknown            | 30  | 700          |               | PENDING_REVIEW
        Standard age, above threshold, known unstable income     | 30  | 700          | false         | REJECTED
        Just under senior age, standard threshold still applies | 64  | 620          | true          | REJECTED
        At senior age boundary, lower threshold applies         | 65  | 620          | true          | APPROVED
        Senior, at lower threshold boundary                     | 65  | 600          | true          | REJECTED
        Senior, just above lower threshold                      | 65  | 601          | true          | APPROVED
        Senior, above lower threshold, income unknown            | 70  | 650          |               | PENDING_REVIEW
        """)
    void evaluatesLoanApproval(int age, int creditScore, Boolean stableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, stableIncome));
    }
}
