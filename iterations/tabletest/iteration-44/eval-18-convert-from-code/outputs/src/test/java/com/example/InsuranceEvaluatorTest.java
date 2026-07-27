package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    @Description("""
        A renewal with no claims is auto-approved before risk score or age are
        considered, so it applies even when the age branch would otherwise take
        over. Risk score combines age and claim count; the last two rows sit on
        either side of its internal rejection threshold. No minimum applicant
        age is enforced by this method, so single-digit ages below are used
        purely to land on that threshold.
        """)
    @TableTest("""
        Scenario                                                | Applicant Type   | Age | Claim Count | Decision?
        Renewal with no claims is auto-approved                 | RENEWAL          | 40  | 0           | AUTO_APPROVED
        Auto-approval overrides senior-age eligibility          | RENEWAL          | 70  | 0           | AUTO_APPROVED
        Renewal with existing claims follows normal evaluation  | RENEWAL          | 30  | 1           | APPROVED
        New applicant with no claims still needs risk approval  | NEW              | 30  | 0           | APPROVED
        Risk score at the rejection threshold is approved       | {RENEWAL, NEW}   | 9   | 5           | APPROVED
        Risk score just past the threshold is rejected          | {RENEWAL, NEW}   | 10  | 5           | REJECTED
        """)
    void decidesApplicationOutcome(String applicantType, int age, int claimCount, String decision) {
        EvaluationResult result = new InsuranceEvaluator().evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
    }

    @Description("""
        Covers premium arithmetic for applications that are approved (risk
        score at or below the rejection threshold, and not the renewal
        auto-approval case). Rows with claim count 0 fix applicant type to
        NEW so they don't trigger the auto-approval override tested in
        decidesApplicationOutcome; the other rows show applicant type does
        not affect the premium.
        """)
    @TableTest("""
        Scenario                                            | Applicant Type   | Age | Claim Count | Premium?
        Standard premium for a young applicant               | NEW              | 30  | 0           | 106.00
        Standard premium increases with claim history        | {RENEWAL, NEW}   | 40  | 2           | 168.00
        Standard rate applies just below the senior threshold| NEW              | 64  | 0           | 112.00
        Senior rate applies at the senior age threshold      | NEW              | 65  | 0           | 221.00
        Senior premium increases with claim history          | {RENEWAL, NEW}   | 70  | 1           | 277.00
        """)
    void calculatesPremiumByAgeTier(String applicantType, int age, int claimCount, double premium) {
        EvaluationResult result = new InsuranceEvaluator().evaluateApplication(applicantType, age, claimCount);
        assertEquals(premium, result.premium(), 0.001);
    }
}
