package com.example.loan;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @DisplayName("Decides loan approval from credit score, income stability, and age-based threshold")
    @Description("""
        Approval requires the credit score to be strictly above the applicable threshold; a
        score equal to the threshold does not qualify and is rejected like any below-threshold
        score. Once the score qualifies, a confirmed unstable income (false) is a definite
        rejection - PENDING_REVIEW is reserved for a genuinely unknown (null) income value.
        """)
    @TableTest("""
        Scenario                                             | Applicant Age | Credit Score | Stable Income | Result?
        Standard applicant, above threshold, stable income   | 64            | 651          | true          | APPROVED
        Standard applicant, above threshold, unstable income | 64            | 651          | false         | REJECTED
        Standard applicant, above threshold, income unknown  | 64            | 651          |               | PENDING_REVIEW
        Standard applicant, at the threshold                 | 64            | 650          | {true, false} | REJECTED
        Standard applicant, at the threshold, income unknown | 64            | 650          |               | REJECTED
        Senior applicant, above threshold, stable income     | 65            | 601          | true          | APPROVED
        Senior applicant, above threshold, unstable income   | 65            | 601          | false         | REJECTED
        Senior applicant, above threshold, income unknown    | 65            | 601          |               | PENDING_REVIEW
        Senior applicant, at the threshold                   | 65            | 600          | {true, false} | REJECTED
        Senior applicant, at the threshold, income unknown   | 65            | 600          |               | REJECTED
        Just below the senior age cutoff                     | 64            | 620          | true          | REJECTED
        At the senior age cutoff                             | 65            | 620          | true          | APPROVED
        """)
    void decidesLoanApproval(int applicantAge, int creditScore, Boolean stableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(applicantAge, creditScore, stableIncome));
    }
}
