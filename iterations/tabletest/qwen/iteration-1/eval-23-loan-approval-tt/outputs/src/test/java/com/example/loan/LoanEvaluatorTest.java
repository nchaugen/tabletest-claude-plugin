package com.example.loan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;

public class LoanEvaluatorTest {

    @TableTest("""
        Scenario                            | Age | Credit Score | Stable Income? | Result?
        High score with income              | 30  | 720          | true           | APPROVED
        Senior at threshold with income   | 65  | 600          | true           | APPROVED
        Below threshold without income    | 30  | 580          | false          | REJECTED
        Above threshold without income    | 30  | 720          | false          | REJECTED
        Senior below threshold no income  | 65  | 580          | false          | REJECTED
        Non-senior borderline high score  | 40  | 700          | false          | REJECTED
        Boundary: exactly 650             | 30  | 650          | true           | REJECTED
        Senior at threshold boundary      | 68  | 600          | true           | APPROVED
        Senior just below threshold       | 70  | 599          | false          | REJECTED
        Age at boundary: not yet senior   | 64  | 720          | true           | REJECTED
        Borderline score, not yet senior  | 60  | 651          | false          | REJECTED
        Non-senior above threshold null income | 30 | 720         |                | PENDING_REVIEW
        Senior high score null income     | 65  | 720          |                | PENDING_REVIEW
        Senior borderline null income     | 68  | 651          |                | PENDING_REVIEW
        Non-senior low score null income  | 30  | 550          |                | PENDING_REVIEW
        Senior below threshold null income| 62  | 550          |                | PENDING_REVIEW
        """)
    void evaluateLoan(int age, int creditScore, Boolean stableIncome, ApprovalResult result) {
        LoanEvaluator evaluator = new LoanEvaluator();
        Assertions.assertEquals(result, evaluator.evaluateLoan(age, creditScore, stableIncome));
    }
}
