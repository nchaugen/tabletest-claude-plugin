package com.example.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @DisplayName("Decides loan approval from an age-adjusted credit threshold and income")
    @Description("""
        Assumes the senior threshold also requires a score strictly above 600 (not merely at
        least 600), mirroring the standard rule's strict 'above 650' wording. Assumes stable
        income is still required for senior applicants - the lower threshold changes only the
        score requirement, not the income requirement.
        """)
    @TableTest("""
        Scenario                                          | Customer Age | Credit Score | Stable Income | Result?
        Just below the senior cutoff                       | 64           | 601          | true           | REJECTED
        At the senior cutoff                                | 65           | 601          | true           | APPROVED
        Standard applicant just above the threshold         | 40           | 651          | true           | APPROVED
        Standard applicant at the threshold                 | 40           | 650          | {true, false}  | REJECTED
        Standard applicant at the threshold, income unknown | 40           | 650          |                | REJECTED
        Senior applicant at the lower threshold              | 65           | 600          | true           | REJECTED
        Above threshold with unstable income                 | 40           | 651          | false          | REJECTED
        Above threshold with unknown income                   | 40           | 651          |                | PENDING_REVIEW
        Senior applicant above threshold with unstable income | 65           | 601          | false          | REJECTED
        """)
    void evaluatesLoanApproval(int customerAge, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(customerAge, creditScore, hasStableIncome));
    }
}
