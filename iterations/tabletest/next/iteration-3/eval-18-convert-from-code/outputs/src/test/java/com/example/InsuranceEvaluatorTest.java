package com.example;

import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @Description("""
        Age does not influence this bypass; two representative ages are grouped in one row to show
        that. When either condition fails, the applicant proceeds through the risk-based evaluation
        covered by the tables below.
        """)
    @TableTest("""
        Scenario                     | Applicant Type | Claim Count | Age      | Decision?     | Premium?
        Renewal with no prior claims | RENEWAL        | 0           | {25, 70} | AUTO_APPROVED | 0
        Renewal with a recent claim  | RENEWAL        | 1           | 30       | APPROVED      | 136.0
        New applicant with no claims | NEW            | 0           | 30       | APPROVED      | 106.0
        """)
    void bypassesUnderwritingForACleanRenewal(
            String applicantType, int claimCount, int age, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium());
    }

    @Description("""
        internalRiskScore is not exposed by the public API, so the rejection classification is
        verified together with the age/claim-count combination that reaches the threshold. Claim
        Count is held at 5 so Age alone crosses the boundary. Applicant Type is fixed to a
        non-renewal value throughout, since the auto-approval bypass above already requires zero
        claims, which does not hold here.
        """)
    @TableTest("""
        Scenario                              | Age | Claim Count | Risk Score Threshold (Policy) | Decision? | Premium?
        Risk score at the rejection threshold | 9   | 5           | 75                            | APPROVED  | 250.0
        Risk score just past the threshold    | 10  | 5           | 75                            | REJECTED  | 0
        """)
    void rejectsApplicationsWhoseRiskScoreExceedsTheThreshold(
            int age, int claimCount, int riskScoreThreshold, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium());
    }

    @Description("""
        Claim Count is held at 1 (nonzero, to stay clear of the auto-approval bypass, and low
        enough to keep the risk score far from the rejection threshold above) so Age alone crosses
        the senior boundary. Applicant Type is fixed to a non-renewal value throughout, for the
        same reason as the table above.
        """)
    @TableTest("""
        Scenario                        | Age | Claim Count | Senior Age Threshold (Policy) | Decision? | Premium?
        Day before the senior threshold | 64  | 1           | 65                            | APPROVED  | 142.0
        At the senior threshold         | 65  | 1           | 65                            | APPROVED  | 273.5
        """)
    void appliesSeniorPricingFromTheAgeThreshold(
            int age, int claimCount, int seniorAgeThreshold, String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, claimCount);
        assertEquals(decision, result.decision());
        assertEquals(premium, result.premium());
    }
}
