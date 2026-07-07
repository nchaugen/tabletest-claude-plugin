package com.example.loan;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @Description("""
        Standard applicants need a credit score above 650; senior applicants (65+) need
        only above 600. Approval requires both a qualifying score and stable income of
        true. Missing income data (null) is reported as PENDING_REVIEW when the score
        qualifies.
        Open: when the score does not qualify AND income is missing, this table assumes
        REJECTED wins (the below-threshold rejection rule takes precedence over the
        missing-income rule) - confirm this precedence before implementing.
        """)
    @TableTest("""
        Scenario                                                     | Age | Credit Score | Stable Income | Result?
        Standard threshold, just above, stable income                | 40  | 651          | true          | APPROVED
        Standard threshold, at limit, not approved                   | 40  | 650          | true          | REJECTED
        Standard threshold, below limit, rejected regardless income  | 40  | 600          | {true, false} | REJECTED
        Standard threshold, below limit, missing income              | 40  | 600          |               | REJECTED
        Standard threshold, above limit, no stable income            | 40  | 700          | false         | REJECTED
        Standard threshold, above limit, missing income              | 40  | 700          |               | PENDING_REVIEW
        Senior threshold, just above, stable income                  | 70  | 601          | true          | APPROVED
        Senior threshold, at limit, not approved                     | 70  | 600          | true          | REJECTED
        Senior threshold, below limit, rejected regardless income    | 70  | 550          | {true, false} | REJECTED
        Senior threshold, below limit, missing income                | 70  | 550          |               | REJECTED
        Senior threshold, above limit, missing income                | 70  | 650          |               | PENDING_REVIEW
        Just under senior age uses standard threshold                | 64  | 620          | true          | REJECTED
        Just at senior age uses lower threshold                      | 65  | 620          | true          | APPROVED
        """)
    void evaluatesLoanApplications(int age, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, hasStableIncome));
    }
}
