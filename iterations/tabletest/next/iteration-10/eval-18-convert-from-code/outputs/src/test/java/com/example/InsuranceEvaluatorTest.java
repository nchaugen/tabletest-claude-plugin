package com.example;

import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    @DisplayName("Decides an insurance application's outcome and premium")
    @Description("""
        Auto-approval requires both a renewal applicant type and zero prior claims; either
        condition failing sends the application through risk-based evaluation instead.

        Risk score = (age / 10, integer division) + (claim count * 15). Applications above the
        policy's risk threshold of 75 are rejected. Surviving applications are priced by age
        tier: senior applicants (65+) pay 200 + risk score * 3.5, standard applicants pay
        100 + risk score * 2.0.
        """)
    @TableTest("""
        Scenario                                            | Applicant Type | Age | Claim Count | Decision?     | Premium?
        Renewal customer with no claims                     | RENEWAL        | 30  | 0           | AUTO_APPROVED | 0
        Renewal, no claims, despite a rejectable risk score  | RENEWAL        | 760 | 0           | AUTO_APPROVED | 0
        Renewal customer with a prior claim                 | RENEWAL        | 30  | 1           | APPROVED      | 136.0
        New applicant with no claims                        | NEW            | 30  | 0           | APPROVED      | 106.0
        At the rejection threshold                          | NEW            | 0   | 5           | APPROVED      | 250.0
        Just past the rejection threshold                   | NEW            | 10  | 5           | REJECTED      | 0
        Just below the senior age threshold                 | NEW            | 64  | 0           | APPROVED      | 112.0
        At the senior age threshold                         | NEW            | 65  | 0           | APPROVED      | 221.0
        """)
    void decidesApplicationOutcome(String applicantType, int age, int claimCount, String decision, double premium) {
        EvaluationResult result = new InsuranceEvaluator().evaluateApplication(applicantType, age, claimCount);

        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium());
    }
}
