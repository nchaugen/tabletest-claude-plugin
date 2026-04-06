Now I have what I need. Let me trace the logic before designing the table:

- `riskScore = (age / 10) + (claimCount × 15)`
- Path 1: RENEWAL + claimCount==0 → AUTO_APPROVED, premium=0 (bypasses risk scoring)
- Path 2: riskScore > 75 → REJECTED, premium=0
- Path 3: age ≥ 65 → APPROVED, senior premium = 200.0 + (score × 3.5)
- Path 4: else → APPROVED, standard premium = 100.0 + (score × 2.0)

Key scenarios to cover:
- AUTO_APPROVED priority (including over-65 RENEWAL with no claims — verifies it doesn't fall through to senior path)
- REJECTED by risk score (both RENEWAL-with-claims and new applicant)
- RENEWAL with claims that passes risk check (shows RENEWAL without claimCount==0 falls through)
- Senior APPROVED (age ≥ 65)
- Standard APPROVED (age < 65)

Here's the test class:

```java
import org.junit.jupiter.api.BeforeEach;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private InsuranceEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new InsuranceEvaluator();
    }

    @Description("""
        Risk score = (age / 10) + (claimCount × 15).
        Senior premium (age ≥ 65) = 200.0 + (score × 3.5).
        Standard premium (age < 65) = 100.0 + (score × 2.0).
        AUTO_APPROVED bypasses risk scoring entirely — applies only to RENEWAL with zero claims.
        """)
    @TableTest("""
        Scenario                         | applicantType | age | claimCount | Status?       | Premium?
        Renewal, no claims               | RENEWAL       | 30  | 0          | AUTO_APPROVED | 0.0
        Renewal, no claims, over 65      | RENEWAL       | 70  | 0          | AUTO_APPROVED | 0.0
        Renewal, claims exceed risk limit | RENEWAL      | 30  | 5          | REJECTED      | 0.0
        New applicant, high risk         | NEW           | 30  | 6          | REJECTED      | 0.0
        Renewal, claims but low risk     | RENEWAL       | 40  | 1          | APPROVED      | 138.0
        Senior, no claims                | NEW           | 65  | 0          | APPROVED      | 221.0
        Senior with claim                | NEW           | 70  | 1          | APPROVED      | 277.0
        Standard, no claims              | NEW           | 30  | 0          | APPROVED      | 106.0
        Standard with claims             | NEW           | 40  | 2          | APPROVED      | 168.0
        """)
    void shouldEvaluateApplication(String applicantType, int age, int claimCount,
                                   String expectedStatus, double expectedPremium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(expectedStatus, result.getStatus());
        assertEquals(expectedPremium, result.getPremium(), 0.001);
    }
}
```

A few notes:

**Dependency** — add to `pom.xml` if not present:
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

**Premium values** are all traceable via the formulas in `@Description`:
| Scenario | score | formula | premium |
|---|---|---|---|
| Renewal, 40y, 1 claim | 4+15=19 | 100+(19×2) | 138.0 |
| Senior 65y, 0 claims | 6+0=6 | 200+(6×3.5) | 221.0 |
| Senior 70y, 1 claim | 7+15=22 | 200+(22×3.5) | 277.0 |
| Standard 30y, 0 claims | 3+0=3 | 100+(3×2) | 106.0 |
| Standard 40y, 2 claims | 4+30=34 | 100+(34×2) | 168.0 |

The "Renewal, no claims, over 65" row is the most important priority check — it confirms the AUTO_APPROVED shortcut fires before the senior branch, which a reader might not expect from casual reading of the code.