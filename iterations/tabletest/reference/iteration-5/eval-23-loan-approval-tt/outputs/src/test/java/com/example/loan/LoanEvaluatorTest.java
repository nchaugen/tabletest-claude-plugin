package com.example.loan;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @DisplayName("Decides a loan application from the applicant's age, credit score and income status")
    @Description("""
        The requirement gives "missing income means pending review" and "below-threshold scores are
        rejected regardless of income" as independent rules, and they collide when an unqualifying
        score arrives with unknown income. This table resolves the collision in favour of rejection —
        "regardless of income" is read as covering the unknown case too — and the last row is where a
        reviewer can disagree.
        """)
    @TableTest("""
        Scenario                            | Age | Credit Score | Stable Income | Decision?
        At the standard threshold           | 64  | 650          | true          | REJECTED
        Just above the standard threshold   | 64  | 651          | true          | APPROVED
        At the senior threshold             | 65  | 600          | true          | REJECTED
        Just above the senior threshold     | 65  | 601          | true          | APPROVED
        Qualifying score, income not stable | 64  | 651          | false         | REJECTED
        Qualifying score, income unknown    | 64  | 651          |               | PENDING_REVIEW
        Below threshold, income unknown     | 64  | 650          |               | REJECTED
        """)
    void decidesALoanApplication(
            int age,
            int creditScore,
            Boolean stableIncome,
            ApprovalResult decision) {
        assertEquals(decision, evaluator.evaluateLoan(age, creditScore, stableIncome));
    }
}
