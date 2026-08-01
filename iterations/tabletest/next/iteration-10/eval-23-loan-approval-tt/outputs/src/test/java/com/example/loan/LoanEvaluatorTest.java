package com.example.loan;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @DisplayName("Decides loan approval status from credit score, age, and income stability")
    @Description("""
        Standard applicants are held to a credit score threshold of 650 (exclusive);
        applicants aged 65 or over get a lower threshold of 600 (exclusive).
        Assumption: with income status unknown (null), the outcome is PENDING_REVIEW
        regardless of age or score, since approval or rejection cannot be decided
        without knowing income stability. Assumption: when income stability is known
        to be false, the application is rejected even if the score clears the
        threshold, since approval requires both conditions to hold.
        """)
    @TableTest("""
        Scenario                                                  | Customer Age | Credit Score | Stable Income | Approval?
        Standard applicant scores above the standard threshold    | 30           | 651          | true          | APPROVED
        Standard applicant scores at the standard threshold       | 30           | 650          | true          | REJECTED
        Standard applicant lacks stable income despite high score | 30           | 700          | false         | REJECTED
        Standard applicant scores well below the standard threshold | 30         | 500          | {true, false} | REJECTED
        Senior applicant scores above the senior threshold        | 70           | 601          | true          | APPROVED
        Senior applicant scores at the senior threshold           | 70           | 600          | true          | REJECTED
        Income information is missing                             | {30, 70}     | {700, 500}   |               | PENDING_REVIEW
        """)
    void decidesLoanApprovalStatus(int customerAge, int creditScore, Boolean hasStableIncome,
                                    ApprovalResult approval) {
        assertEquals(approval, evaluator.evaluateLoan(customerAge, creditScore, hasStableIncome));
    }
}
