No build files exist — this is a standalone code request. I'll trace the logic, design the table, and write the test. Let me compute the expected values:

**Risk score** = `(age / 10) + (claimCount * 15)` (integer division)  
**Decision priority:** RENEWAL+0 claims → HIGH RISK → SENIOR → STANDARD

| Scenario | Type | Age | Claims | Risk score | Decision | Premium |
|---|---|---|---|---|---|---|
| Renewal, no claims | RENEWAL | 30 | 0 | 3 | AUTO_APPROVED | 0.0 |
| Renewal, with claims | RENEWAL | 30 | 3 | 48 | APPROVED | 196.0 |
| High risk score | NEW | 10 | 5 | 76 | REJECTED | 0.0 |
| Risk score boundary (75) | NEW | 0 | 5 | 75 | APPROVED | 250.0 |
| Senior applicant | NEW | 70 | 0 | 7 | APPROVED | 224.5 |
| Standard applicant | NEW | 30 | 1 | 18 | APPROVED | 136.0 |

All six rows share the same assertion logic, so one table covers the full decision priority chain. The `@Description` captures the risk score formula (an intermediate value that's private and unassertable but essential for tracing the logic).

```java
import org.junit.jupiter.api.BeforeEach;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.Description;
import org.tabletest.junit.DisplayName;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private InsuranceEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new InsuranceEvaluator();
    }

    @DisplayName("Application evaluation")
    @Description("""
            Risk score = (age / 10) + (claimCount × 15), using integer division.
            Decision priority:
              1. RENEWAL with 0 claims → AUTO_APPROVED, no premium
              2. Risk score > 75 → REJECTED, no premium
              3. Age ≥ 65 → APPROVED, senior premium: 200 + (riskScore × 3.5)
              4. Otherwise → APPROVED, standard premium: 100 + (riskScore × 2.0)
            """)
    @TableTest("""
            Scenario                   | Applicant type | Age | Claims | Decision?     | Premium?
            Renewal, no claims         | RENEWAL        | 30  | 0      | AUTO_APPROVED | 0.0
            Renewal, with claims       | RENEWAL        | 30  | 3      | APPROVED      | 196.0
            High risk score            | NEW            | 10  | 5      | REJECTED      | 0.0
            Risk score boundary (75)   | NEW            | 0   | 5      | APPROVED      | 250.0
            Senior applicant           | NEW            | 70  | 0      | APPROVED      | 224.5
            Standard applicant         | NEW            | 30  | 1      | APPROVED      | 136.0
            """)
    void evaluatesApplication(String applicantType, int age, int claimCount,
                              String decision, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertAll(
            () -> assertEquals(decision, result.getDecision()),
            () -> assertEquals(premium, result.getPremium(), 0.001)
        );
    }
}
```

A few things to confirm before running:
- **Accessors**: adjust `result.getDecision()` and `result.getPremium()` to match your actual `EvaluationResult` API (could be record components like `result.decision()` / `result.premium()`)
- **Dependency**: if the `tabletest-junit` artifact isn't in your `pom.xml`/`build.gradle` yet, check `skills/tabletest/references/dependency-setup.md` for the exact coordinates