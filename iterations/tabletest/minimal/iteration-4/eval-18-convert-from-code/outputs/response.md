Looking at `evaluateApplication`, I can see two distinct concerns: the **routing decision** (which status is returned) and the **premium arithmetic** (how much to charge). I'll separate these into two focused tables.

Let me verify the math for the premium rows first:
- `age=65, claims=0` → riskScore = 6+0=6 → senior: 200+(6×3.5) = **221.00**
- `age=70, claims=2` → riskScore = 7+30=37 → senior: 200+(37×3.5) = **329.50**
- `age=40, claims=0` → riskScore = 4+0=4 → standard: 100+(4×2.0) = **108.00**
- `age=40, claims=2` → riskScore = 4+30=34 → standard: 100+(34×2.0) = **168.00**

Boundary cases for rejection threshold:
- `age=0, claims=5` → riskScore = 0+75=75 → not > 75 → **APPROVED**
- `age=10, claims=5` → riskScore = 1+75=76 → **REJECTED**

```java
import org.tabletest.junit.TableTest;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @DisplayName("Application status decision")
    @TableTest("""
        Scenario                         | Applicant Type | Age | Claim Count | Status?
        Renewal with no claims           | RENEWAL        | 35  | 0           | AUTO_APPROVED
        Renewal with claims - risk eval  | RENEWAL        | 30  | 5           | REJECTED
        Risk score above threshold       | NEW            | 10  | 5           | REJECTED
        Risk score at threshold (75)     | NEW            | 0   | 5           | APPROVED
        Senior applicant                 | NEW            | 65  | 0           | APPROVED
        Standard applicant               | NEW            | 40  | 2           | APPROVED
        """)
    void evaluatesApplicationStatus(String applicantType, int age, int claimCount, String status) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(status, result.getStatus());
    }

    @DisplayName("Premium calculation")
    @TableTest("""
        Scenario              | Applicant Type | Age | Claim Count | Premium?
        Auto-approved renewal | RENEWAL        | 35  | 0           | 0.00
        Rejected - high risk  | NEW            | 10  | 5           | 0.00
        Senior, no claims     | NEW            | 65  | 0           | 221.00
        Senior, with claims   | NEW            | 70  | 2           | 329.50
        Standard, no claims   | NEW            | 40  | 0           | 108.00
        Standard, with claims | NEW            | 40  | 2           | 168.00
        """)
    void calculatesPremium(String applicantType, int age, int claimCount, double premium) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(premium, result.getPremium());
    }
}
```

A few things worth noting:

- The status table includes a `RENEWAL` with claims to confirm it falls through to the risk check — the auto-approve path requires *both* RENEWAL and zero claims.
- The boundary rows (`age=0, claims=5` vs `age=10, claims=5`) pin the `> 75` threshold precisely. `riskScore=75` is approved; `76` is rejected.
- The premium table covers all four paths including the zero-premium cases, so nothing is left untested.
- `internalRiskScore` is private so it can't be a traceability column — the expected premium values are verifiable by hand from the inputs directly.