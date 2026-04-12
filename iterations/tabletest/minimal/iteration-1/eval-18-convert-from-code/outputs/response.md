No build files in this workspace — you'll need to add the `tabletest-junit` dependency manually. Now let me design and write the tests.

**Analysis of `evaluateApplication`:**

The method has three decision paths:
1. **Auto-approval**: `RENEWAL` + 0 claims → `AUTO_APPROVED`, premium=0
2. **Rejection**: `riskScore > 75` → `REJECTED`, premium=0
3. **Approval**: standard premium (`100 + riskScore × 2.0`) or senior premium (`200 + riskScore × 3.5`) based on age ≥ 65

The intermediate `riskScore = (age / 10) + (claimCount * 15)` uses integer division — worth making visible as a traceability column so failures point directly to which step went wrong.

Risk score boundary: score=75 → APPROVED, score=76 → REJECTED. Concrete values: age=9, claims=5 → `0 + 75 = 75`; age=10, claims=5 → `1 + 75 = 76`.

```java
import org.junit.jupiter.api.BeforeEach;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsuranceEvaluatorTest {

    private InsuranceEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new InsuranceEvaluator();
    }

    @DisplayName("Approval outcome")
    @Description("""
        Risk score formula: (age / 10) + (claimCount * 15), using integer division for age.
        Rejection threshold: riskScore > 75 (score of 75 is not rejected).
        Senior threshold: age >= 65.
        Senior premium: 200 + riskScore × 3.5. Standard premium: 100 + riskScore × 2.0.
        AUTO_APPROVED and REJECTED both return premium 0.
        """)
    @TableTest("""
        Scenario                       | applicantType | age | claimCount | Risk Score? | Status?       | Premium?
        Renewal with no claims         | RENEWAL       | 30  | 0          | 3           | AUTO_APPROVED | 0.00
        Renewal with claims            | RENEWAL       | 30  | 2          | 33          | APPROVED      | 166.00
        Risk score just over threshold | NEW           | 10  | 5          | 76          | REJECTED      | 0.00
        Risk score at threshold        | NEW           | 9   | 5          | 75          | APPROVED      | 250.00
        Standard age applicant         | NEW           | 40  | 1          | 19          | APPROVED      | 138.00
        Senior applicant, age 65       | NEW           | 65  | 1          | 21          | APPROVED      | 273.50
        Standard applicant, age 64     | NEW           | 64  | 1          | 21          | APPROVED      | 142.00
        """)
    void approvalOutcome(String applicantType, int age, int claimCount,
                         int expectedRiskScore, String expectedStatus, double expectedPremium) {
        assertEquals(expectedRiskScore, (age / 10) + (claimCount * 15));

        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);

        assertEquals(expectedStatus, result.getStatus());
        assertEquals(expectedPremium, result.getPremium(), 0.01);
    }
}
```

**What each row covers:**

| Row | Decision path tested |
|---|---|
| Renewal with no claims | Auto-approval short-circuit |
| Renewal with claims | Renewal bypasses auto-approval when claims > 0 |
| Risk score just over threshold | Rejection at score=76 (boundary above) |
| Risk score at threshold | Not rejected at score=75 (boundary at) |
| Standard age applicant | Standard premium formula |
| Senior applicant, age 65 | Senior premium formula, age threshold |
| Standard applicant, age 64 | Standard formula at age boundary below 65 |

**`Risk Score?`** is a traceability column — it's verified with `(age / 10) + (claimCount * 15)` in the test body, so when a row fails you can see immediately whether the risk score calculation or the downstream decision is the problem.

**Dependency to add** (Maven):
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version><!-- latest --></version>
    <scope>test</scope>
</dependency>
```
JUnit Jupiter 5.11+ is also required. Adjust `result.getStatus()` and `result.getPremium()` to match the actual accessor names on `EvaluationResult`.