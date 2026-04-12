Here's what I've written — two tables decomposed by concern:

---

**Dependency** (add to `pom.xml`/`build.gradle` if not present):
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

---

**`InsuranceEvaluatorTest.java`**

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.*;

public class InsuranceEvaluatorTest {

    private InsuranceEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new InsuranceEvaluator();
    }

    @DisplayName("Application routing decisions")
    @Description("""
            Risk score = (age / 10) + (claimCount × 15). Rejection threshold is score > 75.
            AUTO_APPROVED requires applicantType RENEWAL with zero claims; that check runs before the risk score.
            """)
    @TableTest("""
            Scenario                  | applicantType | age | claimCount | Status?       | Premium?
            Renewal, no claims        | RENEWAL       | 40  | 0          | AUTO_APPROVED | 0.0
            Renewal, with claims      | RENEWAL       | 40  | 2          | APPROVED      | 168.0
            High risk score           | NEW           | 60  | 5          | REJECTED      | 0.0
            Senior applicant          | NEW           | 70  | 0          | APPROVED      | 224.5
            Standard applicant        | NEW           | 40  | 0          | APPROVED      | 108.0
            """)
    void routesApplicationDecision(String applicantType, int age, int claimCount, String status, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertAll(
            () -> assertEquals(status, result.getStatus()),
            () -> assertEquals(premium, result.getPremium(), 0.001)
        );
    }

    @DisplayName("Premium tier selection")
    @Description("""
            Applicants aged 65+ use the senior formula: 200 + (riskScore × 3.5).
            All others use the standard formula: 100 + (riskScore × 2.0).
            Age 64 vs 65 both produce riskScore 6 (integer division), isolating the tier threshold.
            """)
    @TableTest("""
            Scenario                  | applicantType | age | claimCount | Premium?
            Standard, no claims       | NEW           | 40  | 0          | 108.0
            Standard, multiple claims | NEW           | 40  | 2          | 168.0
            Below senior threshold    | NEW           | 64  | 0          | 112.0
            At senior threshold       | NEW           | 65  | 0          | 221.0
            Senior, with claims       | NEW           | 70  | 1          | 277.0
            """)
    void selectsPremiumTierByAge(String applicantType, int age, int claimCount, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(premium, result.getPremium(), 0.001);
    }
}
```

---

**Design decisions:**

- **Two tables, two concerns**: Table 1 tests which routing branch fires (status + premium as complete observable output). Table 2 isolates the senior/standard premium arithmetic.
- **`RENEWAL` with claims** in Table 1 verifies the auto-approval is only for zero-claim renewals — it falls through to normal rules and gets a standard premium.
- **Age 64 vs 65 boundary** in Table 2 is the key row: integer division means both ages compute riskScore=6, so the premium difference (112.0 vs 221.0) isolates the age threshold alone.
- **`assertAll`** in Table 1 reports both failures if a row is wrong — avoids masking a premium error when the status also fails.