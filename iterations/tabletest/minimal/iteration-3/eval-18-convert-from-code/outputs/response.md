Looking at `evaluateApplication`, there are two separable concerns:
1. **Routing** — which of the four paths is taken (AUTO_APPROVED, REJECTED, APPROVED-standard, APPROVED-senior)
2. **Premium calculation** — the arithmetic for each tier, with the boundary at age 65

Here are the TableTests. I'll also note the required dependency if it's not already in your build file.

```java
import org.tabletest.junit.TableTest;
import org.junit.jupiter.api.Assertions.assertEquals;

public class InsuranceEvaluatorTest {

    @TableTest("""
        Scenario                     | Applicant Type | Age | Claims | Status?       | Premium?
        Renewal with no claims       | RENEWAL        | 40  | 0      | AUTO_APPROVED | 0.0
        High risk score              | NEW            | 40  | 5      | REJECTED      | 0.0
        Standard applicant approved  | NEW            | 30  | 0      | APPROVED      | 106.0
        Senior applicant approved    | NEW            | 70  | 0      | APPROVED      | 224.5
        Renewal with claims approved | RENEWAL        | 40  | 2      | APPROVED      | 168.0
        """)
    void determinesApplicationOutcome(String applicantType, int age, int claims,
                                      String status, double premium) {
        EvaluationResult result = new InsuranceEvaluator().evaluateApplication(applicantType, age, claims);
        assertEquals(status, result.getStatus());
        assertEquals(premium, result.getPremium(), 0.001);
    }

    @Description("Applicant type is NEW for all rows.")
    @TableTest("""
        Scenario                 | Age | Claims | Premium?
        Standard below threshold | 64  | 0      | 112.0
        Senior at threshold      | 65  | 0      | 221.0
        Standard with claims     | 40  | 2      | 168.0
        Senior with claims       | 70  | 2      | 329.5
        """)
    void calculatesPremiumForApprovedApplications(int age, int claims, double premium) {
        EvaluationResult result = new InsuranceEvaluator().evaluateApplication("NEW", age, claims);
        assertEquals("APPROVED", result.getStatus());
        assertEquals(premium, result.getPremium(), 0.001);
    }
}
```

**Premium values verified against the formulas** (`riskScore = age/10 + claims*15`):

| Row | riskScore | Formula | Premium |
|---|---|---|---|
| NEW, 30, 0 | 3 | 100 + 3×2 | 106.0 |
| NEW, 70, 0 | 7 | 200 + 7×3.5 | 224.5 |
| RENEWAL, 40, 2 | 34 | 100 + 34×2 | 168.0 |
| age=64, claims=0 | 6 | 100 + 6×2 | 112.0 |
| age=65, claims=0 | 6 | 200 + 6×3.5 | 221.0 |
| age=70, claims=2 | 37 | 200 + 37×3.5 | 329.5 |

**If `tabletest-junit` isn't in your build file yet:**
```xml
<dependency>
    <groupId>org.tabletest</groupId>
    <artifactId>tabletest-junit</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>
```

Two design notes:
- The routing table covers the RENEWAL bypass (row 1), the risk rejection (row 2), and both APPROVED tiers (rows 3–5) — including the RENEWAL-with-claims case that falls through to normal evaluation.
- The premium table isolates the age-65 boundary (rows 1–2) since that's the interesting rule; rows 3–4 verify the formula holds across different claim counts for each tier.