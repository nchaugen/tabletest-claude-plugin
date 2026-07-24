package com.example.loan;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @Description("""
        Stable income is held at true for every row so the table isolates the
        credit score threshold and how it shifts for senior applicants.
        Income status variations (unstable, missing) are covered separately below.
        """)
    @TableTest("""
        Scenario                                   | Age | Credit Score | Threshold? | Result?
        Standard applicant at threshold             | 40  | 650          | 650        | REJECTED
        Standard applicant just above threshold     | 40  | 651          | 650        | APPROVED
        Just below senior age, standard threshold   | 64  | 620          | 650        | REJECTED
        Senior applicant at threshold               | 65  | 600          | 600        | REJECTED
        Senior applicant just above threshold       | 65  | 601          | 600        | APPROVED
        """)
    void appliesAgeAdjustedCreditScoreThreshold(int age, int creditScore, int threshold, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, true));
    }

    @Description("""
        Age and credit score are fixed at 40 and 700 (above the standard threshold
        of 650) so the table isolates the effect of income status alone. Only
        missing income (null) is explicitly specified as PENDING_REVIEW; confirmed
        unstable income (false) is assumed to result in REJECTED, since approval
        requires stable income to be confirmed true.
        """)
    @TableTest("""
        Scenario                  | Age | Credit Score | Stable Income | Result?
        Stable income confirmed   | 40  | 700          | true          | APPROVED
        Unstable income confirmed | 40  | 700          | false         | REJECTED
        Missing income info       | 40  | 700          |               | PENDING_REVIEW
        """)
    void handlesIncomeStatus(int age, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, hasStableIncome));
    }

    @Description("""
        Establishes precedence when a below-threshold score and missing income
        coincide. Assumption: the below-threshold rejection rule applies
        "regardless of income" (including unknown income), so it outranks the
        missing-income PENDING_REVIEW rule. The second row is the contrasting
        case already covered above, included here to make the precedence visible.
        """)
    @TableTest("""
        Scenario                                       | Age | Credit Score | Stable Income | Result?
        Below-threshold score outranks missing income  | 40  | 600          |               | REJECTED
        Missing income within threshold                | 40  | 700          |               | PENDING_REVIEW
        """)
    void belowThresholdScoreOutranksMissingIncome(int age, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(age, creditScore, hasStableIncome));
    }
}
