package com.example.loan;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @Description("""
        Senior applicants are those aged 65 and older; younger applicants are standard.
        Assumption: a below-threshold score rejects the applicant even when income status
        is unknown - the "regardless of income" rule takes precedence over the missing-income
        rule. Assumption: a qualifying score with confirmed non-stable income is rejected,
        since the spec only defines PENDING_REVIEW for unknown (not false) income.
        """)
    @TableTest("""
        Scenario                                        | Customer Age | Credit Score | Stable Income | Result?
        Standard age, just above the 650 threshold       | 40           | 651          | true           | APPROVED
        Standard age, at the 650 threshold                | 40           | 650          | {true, false}  | REJECTED
        Standard age, at the threshold, income unknown    | 40           | 650          |                | REJECTED
        Senior age, at the 600 threshold                  | 65           | 600          | {true, false}  | REJECTED
        Just below senior age, standard threshold applies | 64           | 610          | true           | REJECTED
        At senior age, lower 600 threshold applies         | 65           | 610          | true           | APPROVED
        Qualifying score, unstable income                 | 40           | 700          | false          | REJECTED
        Qualifying score, income unknown                  | 40           | 700          |                | PENDING_REVIEW
        """)
    void evaluatesLoanApproval(int customerAge, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(customerAge, creditScore, hasStableIncome));
    }
}
