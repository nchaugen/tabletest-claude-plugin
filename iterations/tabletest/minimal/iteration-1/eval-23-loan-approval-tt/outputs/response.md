Here are the TableTests:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanApprovalTest {

    @DisplayName("Standard applicant loan approval (age under 65)")
    @Description("""
            Credit score threshold for standard applicants is 650 — score must be strictly above 650.
            Open: when income is null AND score is below threshold, which rule wins —
            PENDING_REVIEW (income rule) or REJECTED (score rule)?
            Note: method signature declares primitive boolean but null income requires Boolean (boxed).
            """)
    @TableTest("""
            Scenario                            | age | creditScore | hasStableIncome | Result?
            Approved — above threshold          | 35  | 700         | true            | APPROVED
            Approved — just above threshold     | 35  | 651         | true            | APPROVED
            Rejected — at threshold (not above) | 35  | 650         | {true, false}   | REJECTED
            Rejected — below threshold          | 35  | 500         | {true, false}   | REJECTED
            Rejected — no stable income         | 35  | 700         | false           | REJECTED
            Pending — income not provided       | 35  | 700         |                 | PENDING_REVIEW
            Age 64 — non-senior threshold holds | 64  | 630         | true            | REJECTED
            """)
    void standardApplicantDecision(int age, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluateLoan(age, creditScore, hasStableIncome));
    }

    @DisplayName("Senior applicant loan approval (age 65 and over)")
    @Description("""
            Senior applicants (age 65+) are approved at a lower credit score threshold of 600 —
            score must be strictly above 600. The age-65 boundary row confirms which threshold applies.
            Open: same conflict as standard table — null income + below-threshold score precedence
            is unspecified.
            """)
    @TableTest("""
            Scenario                               | age | creditScore | hasStableIncome | Result?
            Approved — well above senior threshold | 75  | 700         | true            | APPROVED
            Approved — just above senior threshold | 65  | 601         | true            | APPROVED
            Approved — score that fails standard   | 65  | 630         | true            | APPROVED
            Rejected — at senior threshold         | 65  | 600         | {true, false}   | REJECTED
            Rejected — just below senior threshold | 65  | 599         | {true, false}   | REJECTED
            Rejected — no stable income            | 70  | 650         | false           | REJECTED
            Pending — income not provided          | 65  | 650         |                 | PENDING_REVIEW
            """)
    void seniorApplicantDecision(int age, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluateLoan(age, creditScore, hasStableIncome));
    }
}
```

**Key design decisions:**

- **Two tables** decomposed by age group rather than one large table with a threshold traceability column — this directly tests the age boundary behavior
- **`{true, false}` value sets** on `hasStableIncome` for below-threshold rows, asserting rejection is truly independent of income
- **`age 64` row** in the standard table and **`age 65, score 630`** in the senior table together demonstrate the threshold switch at the age boundary
- **Blank cell** for null income (boxed `Boolean` parameter required) — the method signature must change from `boolean` to `Boolean`
- **Two open questions** flagged in `@Description`: strictly-above semantics for score=600/650, and the undefined precedence when both null-income and below-threshold apply simultaneously