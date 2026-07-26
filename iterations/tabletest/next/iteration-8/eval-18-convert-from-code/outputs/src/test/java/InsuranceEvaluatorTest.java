import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @DisplayName("Application eligibility decision")
    @Description("""
        Applications are limited to ages 18-75 inclusive; anyone outside that
        range is declined regardless of other factors. Presence of "terminal
        illness" among pre-existing conditions is treated as an outright
        disqualifying condition. When both the age check and the
        disqualifying-condition check fail, age is evaluated first and its
        reason wins.
        Smoker status and non-disqualifying conditions do not affect this
        decision, so they are held at their most favorable values
        (non-smoker, no conditions) here - see risk tier classification
        below for that rule.
        """)
    @TableTest("""
        Scenario                                    | Age | Smoker | Conditions          | Coverage Amount | Decision? | Decline Reason?
        Below minimum insurable age                 | 17  | false  | []                  | 100000          | DECLINED  | Applicant age outside insurable range (18-75)
        At minimum insurable age                    | 18  | false  | []                  | 100000          | APPROVED  |
        At maximum insurable age                    | 75  | false  | []                  | 100000          | APPROVED  |
        Above maximum insurable age                 | 76  | false  | []                  | 100000          | DECLINED  | Applicant age outside insurable range (18-75)
        Disqualifying condition present             | 40  | false  | [terminal illness]  | 100000          | DECLINED  | Applicant has a disqualifying pre-existing condition
        Age failure takes precedence over condition | 10  | false  | [terminal illness]  | 100000          | DECLINED  | Applicant age outside insurable range (18-75)
        """)
    void decidesEligibility(int age, boolean smoker, List<String> conditions, BigDecimal coverageAmount,
                            Decision expectedDecision, String expectedReason) {
        var application = new InsuranceApplication(age, smoker, conditions, coverageAmount);
        var result = evaluator.evaluateApplication(application);
        assertEquals(expectedDecision, result.decision());
        assertEquals(expectedReason, result.declineReason());
    }

    @DisplayName("Risk tier classification")
    @Description("""
        For applicants who pass the eligibility check (age fixed at 40, no
        disqualifying condition), risk tier depends on smoker status and the
        number of non-disqualifying pre-existing conditions: 0 conditions is
        STANDARD (or ELEVATED if smoking), 1-2 conditions raises that by one
        level (ELEVATED, or HIGH if smoking), and 3 or more conditions is
        referred for manual underwriting regardless of smoker status.
        """)
    @TableTest("""
        Scenario                                       | Smoker        | Conditions                      | Decision? | Risk Tier?
        Non-smoker, no conditions                       | false         | []                              | APPROVED  | STANDARD
        Non-smoker, just below referral threshold       | false         | [asthma, allergies]             | APPROVED  | ELEVATED
        Smoker, no conditions                           | true          | []                              | APPROVED  | ELEVATED
        Smoker, just below referral threshold           | true          | [asthma, allergies]             | APPROVED  | HIGH
        At referral threshold, refers to underwriting   | {true, false} | [asthma, allergies, back pain]  | REFERRED  |
        """)
    void classifiesRiskTier(boolean smoker, List<String> conditions,
                            Decision expectedDecision, RiskTier expectedTier) {
        var application = new InsuranceApplication(40, smoker, conditions, new BigDecimal("100000"));
        var result = evaluator.evaluateApplication(application);
        assertEquals(expectedDecision, result.decision());
        assertEquals(expectedTier, result.riskTier());
    }

    @DisplayName("Premium calculation")
    @Description("""
        For approved applications, premium is the coverage amount charged at
        a per-mille rate that increases with risk tier: 0.50 for STANDARD,
        1.00 for ELEVATED, and 2.00 for HIGH, expressed to 2 decimal places.
        Age is fixed at 40 and plays no part in this calculation; smoker
        status and conditions here are chosen only to reach the tier under
        test (see risk tier classification above for that rule).
        """)
    @TableTest("""
        Scenario                     | Smoker | Conditions | Coverage Amount | Risk Tier? | Premium?
        Standard tier, full coverage | false  | []         | 100000          | STANDARD   | 50.00
        Standard tier, less coverage | false  | []         | 40000           | STANDARD   | 20.00
        Standard tier, no coverage   | false  | []         | 0               | STANDARD   | 0.00
        Elevated tier, full coverage | true   | []         | 100000          | ELEVATED   | 100.00
        High tier, full coverage     | true   | [asthma]   | 100000          | HIGH       | 200.00
        """)
    void calculatesPremium(boolean smoker, List<String> conditions, BigDecimal coverageAmount,
                           RiskTier expectedTier, BigDecimal expectedPremium) {
        var application = new InsuranceApplication(40, smoker, conditions, coverageAmount);
        var result = evaluator.evaluateApplication(application);
        assertEquals(expectedTier, result.riskTier());
        assertEquals(0, expectedPremium.compareTo(result.premium()));
    }
}
