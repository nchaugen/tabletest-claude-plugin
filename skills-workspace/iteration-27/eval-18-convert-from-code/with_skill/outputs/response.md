```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsuranceEvaluatorTest {

    private InsuranceEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new InsuranceEvaluator();
    }

    @DisplayName("Application evaluation")
    @Description("""
        Risk score = (age / 10) + (claimCount × 15). Rejection threshold: risk score > 75.
        AUTO_APPROVED path (RENEWAL + 0 claims) bypasses the risk check entirely.
        Senior premium (age ≥ 65): 200 + riskScore × 3.5.
        Standard premium (age < 65): 100 + riskScore × 2.0.
        """)
    @TableTest("""
        Scenario                          | Applicant type | Age | Claims | Status?       | Premium?
        Renewal, no claims                | RENEWAL        | 30  | 0      | AUTO_APPROVED | 0.0
        Renewal, no claims, senior age    | RENEWAL        | 70  | 0      | AUTO_APPROVED | 0.0
        Renewal with claims               | RENEWAL        | 40  | 2      | APPROVED      | 168.0
        High risk                         | NEW            | 20  | 5      | REJECTED      | 0.0
        High risk, senior age             | NEW            | 65  | 5      | REJECTED      | 0.0
        Senior, within risk threshold     | NEW            | 65  | 1      | APPROVED      | 273.5
        Standard, within risk threshold   | NEW            | 30  | 1      | APPROVED      | 136.0
        """)
    void evaluatesApplication(String applicantType, int age, int claimCount,
                               String status, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(status, result.getStatus());
        assertEquals(premium, result.getPremium(), 0.001);
    }
}
```

A few design notes:

**Two RENEWAL rows** — the second (`senior age`) shows that `AUTO_APPROVED` bypasses the senior pricing path entirely, which is non-obvious from reading the code.

**`Renewal with claims`** — shows RENEWAL isn't magic when `claimCount > 0`; it falls through to normal risk scoring. (riskScore = 4 + 30 = 34 → standard premium: 100 + 68 = 168.0)

**`High risk, senior age`** — shows the risk check fires before the senior branch. A rejected senior applicant still gets 0 premium. (riskScore = 6 + 75 = 81)

**Risk score as `@Description`** — since `internalRiskScore` is private, it can't be asserted. The formula in `@Description` gives readers enough to verify any row independently without cluttering the table.

If you don't have the dependency yet, add to `pom.xml`:
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```
Check Maven Central for the current version.