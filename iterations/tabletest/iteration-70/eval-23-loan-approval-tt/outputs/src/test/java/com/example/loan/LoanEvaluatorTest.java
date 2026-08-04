package com.example.loan;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @Description("""
        Score must exceed the applicable threshold, not merely reach it. Senior applicants
        (age 65+) use a lower threshold (600) than regular applicants (650). A score at or
        below the applicable threshold is rejected regardless of income, including unknown
        income. Above the threshold, known unstable income (false) is rejected and unknown
        income (null) is sent to pending review; only known stable income (true) approves.
        """)
    @TableTest("""
        Scenario                                        | Customer Age | Credit Score | Stable Income | Approval?
        Regular applicant, score at the threshold       | 40           | 650          | true          | REJECTED
        Regular applicant, score just above threshold   | 40           | 651          | true          | APPROVED
        Just under the senior age                       | 64           | 601          | true          | REJECTED
        At the senior age                               | 65           | 601          | true          | APPROVED
        Senior applicant, score at the senior threshold | 65           | 600          | true          | REJECTED
        Low score, income known                         | 40           | 500          | {true, false} | REJECTED
        Low score, income unknown                       | 40           | 500          |               | REJECTED
        Sufficient score, unstable income               | 40           | 700          | false         | REJECTED
        Sufficient score, unknown income                | 40           | 700          |               | PENDING_REVIEW
        """)
    void decidesLoanApprovalStatus(int customerAge, int creditScore, Boolean hasStableIncome,
                                    ApprovalResult approval) {
        assertEquals(approval, evaluator.evaluateLoan(customerAge, creditScore, hasStableIncome));
    }
}
