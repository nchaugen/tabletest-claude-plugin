package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @Description("""
        A renewal with zero claims is auto-approved without a premium, no matter the
        applicant's age. Both the applicant type and the claim count must match for this
        to trigger — either one alone falls through to standard evaluation.
        """)
    @TableTest("""
        Scenario                                     | Applicant Type | Age                | Claim Count | Decision?     | Premium?
        Renewal with no claims, regardless of age     | RENEWAL        | {18, 40, 65, 90}   | 0           | AUTO_APPROVED | 0.00
        New applicant with no claims is not auto-approved | NEW         | 30                 | 0           | APPROVED      | 106.00
        Renewal with a claim is not auto-approved      | RENEWAL        | 30                 | 1           | APPROVED      | 136.00
        """)
    void autoApprovesRenewalsWithNoClaims(String applicantType, int age, int claimCount,
                                          String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }

    @Description("""
        Risk score is derived from age and claim count and rejects the application once it
        exceeds 75, regardless of applicant type. Age 9 and age 10 both carry 5 claims so the
        only thing that changes between rows is which side of the threshold the risk score
        lands on.
        """)
    @TableTest("""
        Scenario                          | Applicant Type    | Age | Claim Count | Decision? | Premium?
        At the rejection threshold        | {NEW, RENEWAL}    | 9   | 5           | APPROVED  | 250.00
        Just over the rejection threshold | {NEW, RENEWAL}    | 10  | 5           | REJECTED  | 0.00
        """)
    void rejectsApplicationsAboveRiskThreshold(String applicantType, int age, int claimCount,
                                                String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }

    @Description("""
        Approved applicants are priced with one of two formulas depending on age: under 65
        uses the standard rate, 65 and over uses the senior rate. Ages 64 and 65 are chosen
        because integer division gives both the same underlying risk score (6), isolating
        the formula switch from any change in risk score.
        """)
    @TableTest("""
        Scenario                      | Applicant Type | Age | Claim Count | Decision? | Premium?
        Just under the senior age tier | NEW           | 64  | 0           | APPROVED  | 112.00
        At the senior age tier          | NEW           | 65  | 0           | APPROVED  | 221.00
        """)
    void selectsPremiumFormulaByAgeTier(String applicantType, int age, int claimCount,
                                        String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium(), 0.001);
    }
}
