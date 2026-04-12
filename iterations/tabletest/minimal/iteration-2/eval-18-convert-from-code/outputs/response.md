There's no existing project — I'll write the test class from scratch. Let me trace the logic precisely:

- **riskScore** = `(age/10) + (claimCount×15)`
- Branch 1: `RENEWAL` + `claimCount==0` → `AUTO_APPROVED`, premium=0
- Branch 2: `riskScore > 75` → `REJECTED`, premium=0
- Branch 3: `age >= 65` → `APPROVED`, senior premium = `200 + (riskScore×3.5)`
- Branch 4: otherwise → `APPROVED`, standard premium = `100 + (riskScore×2.0)`

Key concern worth noting: a `RENEWAL` applicant with `claimCount > 0` falls through to the risk check and **can be rejected**.

Two concerns → two tables: the **approval decision** (which branch fires) and the **premium arithmetic** (what amount is charged for approved cases).

**Verified arithmetic:**
| Age | Claims | riskScore | Formula | Premium |
|-----|--------|-----------|---------|---------|
| 30 | 0 | 3 | 100 + 6 | 106.0 |
| 40 | 2 | 34 | 100 + 68 | 168.0 |
| 30 | 4 | 63 | 100 + 126 | 226.0 |
| 65 | 0 | 6 | 200 + 21 | 221.0 |
| 70 | 1 | 22 | 200 + 77 | 277.0 |

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsuranceEvaluatorTest {

    private final InsuranceEvaluator evaluator = new InsuranceEvaluator();

    @DisplayName("Approval decision")
    @Description("""
            Risk score = (age / 10) + (claimCount × 15) using integer division.
            Rejection threshold: risk score > 75.
            Senior threshold: age ≥ 65.
            RENEWAL applicants with zero claims skip the risk check entirely.
            RENEWAL applicants with claims do fall through and can be rejected.
            """)
    @TableTest("""
            Scenario                                  | Applicant Type | Age | Claim Count | Status?
            Renewal, no claims auto-approved          | RENEWAL        | 40  | 0           | AUTO_APPROVED
            Renewal, with claims falls through        | RENEWAL        | 30  | 1           | APPROVED
            Renewal, high risk score rejected         | RENEWAL        | 30  | 5           | REJECTED
            Non-renewal, high risk score rejected     | NEW            | 30  | 5           | REJECTED
            Standard applicant, low risk approved     | NEW            | 40  | 0           | APPROVED
            Senior threshold, low risk approved       | NEW            | 65  | 0           | APPROVED
            """)
    void approvalDecision(String applicantType, int age, int claimCount, String expectedStatus) {
        EvaluationResult result = evaluator.evaluateApplication(applicantType, age, claimCount);
        assertEquals(expectedStatus, result.getStatus());
    }

    @DisplayName("Approved premium amount")
    @Description("""
            Standard premium (age < 65):  100 + (riskScore × 2.0)
            Senior premium  (age ≥ 65):   200 + (riskScore × 3.5)
            where riskScore = (age / 10) + (claimCount × 15).
            All rows use applicant type NEW with risk score ≤ 75 to reach the APPROVED branch.
            """)
    @TableTest("""
            Scenario                      | Age | Claim Count | Premium?
            Standard, no claims           | 30  | 0           | 106.0
            Standard, with claims         | 40  | 2           | 168.0
            Standard, near risk ceiling   | 30  | 4           | 226.0
            Senior threshold, no claims   | 65  | 0           | 221.0
            Senior, with claims           | 70  | 1           | 277.0
            """)
    void approvedPremium(int age, int claimCount, double expectedPremium) {
        EvaluationResult result = evaluator.evaluateApplication("NEW", age, claimCount);
        assertEquals(expectedPremium, result.getPremium());
    }
}
```

A few things to adjust for your project:

- **`result.getStatus()` / `result.getPremium()`** — adjust to match your `EvaluationResult` accessors (e.g., `status()` / `premium()` if it's a record).
- **Dependency** — add `org.tabletest:tabletest-junit` to your `pom.xml` or `build.gradle` if it isn't already there, with JUnit Jupiter 5.11+.
- **Package** — add your package declaration at the top.

The "Renewal, high risk score rejected" row (RENEWAL + 5 claims → riskScore=78 → REJECTED) is probably the most surprising behaviour in the class — it's easy to assume `RENEWAL` always gets approved. That scenario is worth a code comment in `evaluateApplication` as well.