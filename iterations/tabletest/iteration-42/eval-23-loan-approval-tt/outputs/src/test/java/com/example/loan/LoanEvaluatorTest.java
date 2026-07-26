package com.example.loan;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @DisplayName("Loan approval decision")
    @Description("""
        Standard applicants need a credit score above 650; applicants aged 65+ get
        a lower threshold of 600. Scores at the threshold count as below it (the
        rule is "above", not "at or above").

        Open assumption: "below-threshold scores are rejected regardless of income"
        is read to include unknown income, so a below-threshold score rejects even
        when income is unknown. PENDING_REVIEW only applies once the score clears
        the threshold but income is still unknown. A cleared score with income
        known to be unstable (explicit false, not null) rejects by elimination,
        since approval requires stable income and PENDING_REVIEW is reserved for
        the unknown case.
        """)
    @TableTest("""
        Scenario                                              | Age | Credit Score | Stable Income | Result?
        Standard applicant, score just above threshold        | 30  | 651          | true           | APPROVED
        Standard applicant, score at threshold (not above)     | 30  | 650          | {true, false}  | REJECTED
        Standard applicant, score at threshold, income unknown | 30  | 650          |                | REJECTED
        Standard applicant, score above threshold, income unknown | 30 | 651        |                | PENDING_REVIEW
        Standard applicant, score above threshold, not stable  | 30  | 651          | false          | REJECTED
        Senior applicant, score at lower threshold (not above) | 65  | 600          | true           | REJECTED
        Senior applicant, score just above lower threshold     | 65  | 601          | true           | APPROVED
        Just under senior age, senior-range score               | 64  | 601          | true           | REJECTED
        """)
    void evaluatesLoanApproval(int age, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, hasStableIncome));
    }
}
