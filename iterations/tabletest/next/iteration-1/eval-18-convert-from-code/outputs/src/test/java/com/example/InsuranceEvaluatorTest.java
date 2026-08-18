package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    @Description("""
        Applicant type only matters as the exact string "RENEWAL" combined with zero claims,
        which auto-approves regardless of age or risk; otherwise the type has no effect on the
        decision or premium. The rejection threshold and the two premium formulas are computed
        from an internal risk score that the API never exposes, so decision and premium are
        verified together per row here; an accessor for that score would let the risk
        classification and the premium arithmetic be split into their own tables. The age
        9/claims 5 and age 10/claims 5 rows straddle that internal threshold from both sides;
        age 64 and 65 (same claim count) hold the score fixed to isolate the senior-premium age
        boundary.
        """)
    @TableTest("""
        Scenario                                      | Applicant Type  | Age | Claim Count | Decision?     | Premium? | Throws?
        Renewal application with no claims            | RENEWAL         | 70  | 0           | AUTO_APPROVED | 0.0      |
        Renewal application with a claim              | RENEWAL         | 70  | 1           | APPROVED      | 277.0    |
        Non-renewal application with no claims        | {NEW, EXISTING} | 70  | 0           | APPROVED      | 224.5    |
        Risk score at the rejection threshold         | NEW             | 9   | 5           | APPROVED      | 250.0    |
        Risk score just past the rejection threshold  | NEW             | 10  | 5           | REJECTED      | 0.0      |
        Applicant just below the senior age threshold | {NEW, EXISTING} | 64  | 0           | APPROVED      | 112.0    |
        Applicant at the senior age threshold         | {NEW, EXISTING} | 65  | 0           | APPROVED      | 221.0    |
        Null applicant type                           |                 | 30  | 0           |               |          | java.lang.NullPointerException
        """)
    void evaluatesApplicationOutcome(String applicantType, int age, int claimCount,
            String decision, Double premium, Class<? extends Throwable> throws_) {
        Outcome outcome = evaluate(applicantType, age, claimCount);
        assertEquals(throws_, outcome.thrown());
        assertEquals(decision, outcome.decision());
        assertEquals(premium, outcome.premium());
    }

    private static Outcome evaluate(String applicantType, int age, int claimCount) {
        try {
            EvaluationResult result = new InsuranceEvaluator().evaluateApplication(applicantType, age, claimCount);
            return new Outcome(result.decision(), result.premium(), null);
        } catch (Throwable thrown) {
            return new Outcome(null, null, thrown.getClass());
        }
    }

    private record Outcome(String decision, Double premium, Class<? extends Throwable> thrown) {}
}
