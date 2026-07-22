package com.example.loan;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @Description("""
        Standard applicants need a credit score above 650 to be approved.
        Senior applicants (age 65 and older) qualify with a lower threshold
        of 600. Stable income is held true throughout every row so the
        result variation is driven only by the age-based threshold rule;
        income handling is covered separately below.
        """)
    @TableTest("""
        Scenario                                        | Age | Credit Score | Result?
        Non-senior, just below threshold                | 64  | 650          | REJECTED
        Non-senior, just above threshold                 | 64  | 651          | APPROVED
        At senior cutoff, at lowered threshold           | 65  | 600          | REJECTED
        At senior cutoff, above lowered threshold        | 65  | 601          | APPROVED
        Just below senior cutoff, standard threshold     | 64  | 601          | REJECTED
        Well above senior cutoff                         | 90  | 601          | APPROVED
        """)
    void appliesAgeBasedCreditScoreThreshold(int age, int creditScore, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, true));
    }

    @Description("""
        Applies to a standard (non-senior) applicant; the age-based threshold
        rule is covered by appliesAgeBasedCreditScoreThreshold above, so age
        is fixed at a representative non-senior value here.

        Assumption: the "rejected regardless of income" rule takes priority
        over the missing-income rule, so a below-threshold score is REJECTED
        even when income is unknown -- PENDING_REVIEW only applies once the
        score clears the threshold.
        """)
    @TableTest("""
        Scenario                        | Credit Score | Stable Income | Result?
        Above threshold, stable income  | 700          | true          | APPROVED
        Above threshold, unstable income| 700          | false         | REJECTED
        Above threshold, unknown income | 700          |               | PENDING_REVIEW
        Below threshold, known income   | 600          | {true, false} | REJECTED
        Below threshold, unknown income | 600          |               | REJECTED
        """)
    void determinesResultFromIncomeStatus(int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(40, creditScore, hasStableIncome));
    }
}
