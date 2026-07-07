package com.example.loan;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @Description("""
        Standard applicants are approved above a credit score of 650; senior
        applicants (age 65+) are approved above a lower threshold of 600. The
        senior age cutoff is inclusive at 65.

        Assumption: "below-threshold scores are rejected regardless of income"
        is read to cover missing (null) income too, not just known true/false
        values — a below-threshold score always rejects, even when income is
        unknown.

        Assumption: PENDING_REVIEW is reserved for missing (null) income
        information only. A qualifying score with explicitly unstable income
        (false) is REJECTED, since approval requires stable income to be true.
        """)
    @TableTest("""
        Scenario                                                 | Age | Credit Score | Stable Income | Result?
        At the standard threshold, not above it                  | 40  | 650          | true           | REJECTED
        Just above the standard threshold                        | 40  | 651          | true           | APPROVED
        Not yet senior, standard threshold still applies          | 64  | 610          | true           | REJECTED
        Senior age boundary, lower threshold applies              | 65  | 610          | true           | APPROVED
        Senior applicant, at the lower threshold, not above it    | 70  | 600          | true           | REJECTED
        Senior applicant, just above the lower threshold          | 70  | 601          | true           | APPROVED
        Qualifying score with unstable income                     | 40  | 700          | false          | REJECTED
        Qualifying score with missing income information          | 40  | 700          |                | PENDING_REVIEW
        Senior qualifying score with missing income information   | 70  | 610          |                | PENDING_REVIEW
        Below threshold rejects regardless of stable income       | 40  | 500          | {true, false}  | REJECTED
        Below threshold rejects even with missing income info     | 40  | 500          |                | REJECTED
        """)
    void evaluatesLoanApproval(int age, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, hasStableIncome));
    }
}
