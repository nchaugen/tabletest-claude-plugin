package com.example.loan;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @Description("""
        Standard applicants must score strictly above 650 to be approved.
        Stable income is held constant (true) here since this table is about
        the age-based threshold, not the income rule.
        """)
    @TableTest("""
        Scenario                                     | Customer Age | Credit Score | Approval?
        Standard applicant at the threshold           | 40           | 650          | REJECTED
        Standard applicant just above the threshold   | 40           | 651          | APPROVED
        Just below senior age keeps standard threshold| 64           | 601          | REJECTED
        Senior applicant at the senior threshold      | 65           | 600          | REJECTED
        Senior applicant just above the senior threshold | 65        | 601          | APPROVED
        """)
    void seniorApplicantsGetALowerThreshold(String scenario, int customerAge, int creditScore, ApprovalResult approval) {
        assertEquals(approval, evaluator.evaluateLoan(customerAge, creditScore, true));
    }

    @Description("""
        Customer age is held constant (40, a standard applicant) and the credit
        score always qualifies (700, above the standard threshold of 650) since
        this table is about the effect of income status, not the threshold.
        """)
    @TableTest("""
        Scenario                                | Has Stable Income | Approval?
        Stable income with a qualifying score    | true              | APPROVED
        No stable income despite a qualifying score | false          | REJECTED
        Unknown income status                   |                   | PENDING_REVIEW
        """)
    void incomeStatusDeterminesOutcomeForAQualifyingScore(String scenario, Boolean hasStableIncome, ApprovalResult approval) {
        assertEquals(approval, evaluator.evaluateLoan(40, 700, hasStableIncome));
    }

    @Description("""
        Age is held constant (40, a standard applicant) and the credit score
        (600) is below the standard threshold of 650. This table exists only
        to show that rejection for a below-threshold score outranks the
        pending-review rule for unknown income - the two rules covered
        separately above.
        """)
    @TableTest("""
        Scenario                                       | Has Stable Income | Approval?
        Below threshold regardless of known income status | {true, false}  | REJECTED
        Below threshold with unknown income            |                   | REJECTED
        """)
    void belowThresholdScoreIsRejectedRegardlessOfIncome(String scenario, Boolean hasStableIncome, ApprovalResult approval) {
        assertEquals(approval, evaluator.evaluateLoan(40, 600, hasStableIncome));
    }
}
