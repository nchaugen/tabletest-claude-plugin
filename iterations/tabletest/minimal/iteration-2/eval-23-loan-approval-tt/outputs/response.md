Here is the complete `LoanApprovalTest.java`:

```java
// Dependency required in pom.xml:
//   <dependency>
//     <groupId>org.tabletest</groupId>
//     <artifactId>tabletest-junit</artifactId>
//     <version>VERSION</version>
//     <scope>test</scope>
//   </dependency>
//
// Note: hasStableIncome must be Boolean (boxed), not boolean (primitive),
// to accept null for the missing-income PENDING_REVIEW case.

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoanApprovalTest {

    private final LoanService loanService = new LoanService();

    @DisplayName("Standard applicant credit threshold")
    @Description("""
            Customer age is 35 for all rows (non-senior).
            Open: 'above 650' — these tests assume strictly above (> 650),
            so 650 is REJECTED and 651 is APPROVED.
            """)
    @TableTest("""
            Scenario                         | Credit Score | Stable Income | Result?
            Just above threshold with income | 651          | true          | APPROVED
            At threshold with income         | 650          | true          | REJECTED
            Well above with income           | 750          | true          | APPROVED
            Above threshold, no income       | 751          | false         | REJECTED
            Below threshold                  | 500          | {true, false} | REJECTED
            """)
    void standardApplicantCreditThreshold(int creditScore, Boolean stableIncome, ApprovalResult result) {
        assertEquals(result, loanService.evaluateLoan(35, creditScore, stableIncome));
    }

    @DisplayName("Senior applicant lower threshold and age boundary")
    @Description("""
            Seniors are applicants aged 65 or above.
            Open: whether the senior threshold is >= 600 or > 600 — these tests assume >= 600 (600 approves).
            Open: whether seniors still require stable income — these tests assume yes.
            """)
    @TableTest("""
            Scenario                                | Customer Age | Credit Score | Stable Income | Result?
            Senior just above senior threshold      | 65           | 601          | true          | APPROVED
            Senior at senior threshold              | 65           | 600          | true          | APPROVED
            Senior just below senior threshold      | 65           | 599          | true          | REJECTED
            Senior above standard threshold too     | 65           | 651          | true          | APPROVED
            Senior without income                   | 65           | 620          | false         | REJECTED
            Senior below threshold                  | 65           | 550          | {true, false} | REJECTED
            Age boundary: non-senior at age 64      | 64           | 600          | true          | REJECTED
            Age boundary: non-senior above standard | 64           | 651          | true          | APPROVED
            """)
    void seniorApplicantLowerThreshold(int customerAge, int creditScore, Boolean stableIncome, ApprovalResult result) {
        assertEquals(result, loanService.evaluateLoan(customerAge, creditScore, stableIncome));
    }

    @DisplayName("Missing income triggers pending review")
    @Description("""
            Income information is absent (null) for all rows — hasStableIncome is null.
            Open: whether a below-threshold score with null income yields PENDING_REVIEW or REJECTED.
            These tests assume PENDING_REVIEW takes precedence over score-based rejection.
            """)
    @TableTest("""
            Scenario                           | Customer Age | Credit Score | Result?
            Standard age, above threshold      | 35           | 751          | PENDING_REVIEW
            Standard age, at threshold         | 35           | 650          | PENDING_REVIEW
            Standard age, below threshold      | 35           | 500          | PENDING_REVIEW
            Senior age, above senior threshold | 65           | 620          | PENDING_REVIEW
            Senior age, below senior threshold | 65           | 550          | PENDING_REVIEW
            """)
    void missingIncomeTriggersReview(int customerAge, int creditScore, ApprovalResult result) {
        assertEquals(result, loanService.evaluateLoan(customerAge, creditScore, null));
    }
}
```

---

**Three tables, three concerns:**

| Table | What it covers |
|---|---|
| `standardApplicantCreditThreshold` | Score > 650 + income required; age fixed at 35. Boundary at 650/651. Value set `{true, false}` asserts below-threshold is rejected regardless of income. |
| `seniorApplicantLowerThreshold` | Age ≥ 65 unlocks lower threshold; boundary rows at age 64/65 and score 599/600/601. Value set for below-threshold regardless of income. |
| `missingIncomeTriggersReview` | Null income → `PENDING_REVIEW` across score ranges and age groups. Income column omitted (always null, passed directly in method body). |

**Two design notes to resolve with stakeholders (flagged in `@Description`):**

1. **Threshold inclusivity** — "above 650" and "lower threshold of 600" are ambiguous. Tests assume `> 650` (standard) and `>= 600` (senior). Flip the boundary rows if it's the other way.
2. **Conflicting rules for null income + below-threshold score** — the spec's PENDING_REVIEW rule and its "rejected regardless of income" rule conflict when both apply. Tests assume PENDING_REVIEW wins.

**Method signature change required:** `hasStableIncome` must be `Boolean` (boxed) to accept `null`. The current `boolean` primitive will not compile against a null argument.