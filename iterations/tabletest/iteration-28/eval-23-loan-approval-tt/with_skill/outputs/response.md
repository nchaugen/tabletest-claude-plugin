Good. Now writing the complete test class.

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanEvaluatorTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @DisplayName("Age-based credit threshold determines approval")
    @Description("""
            Income is true for all APPROVED rows to isolate threshold logic.
            REJECTED rows use {true, false} to assert that income does not affect
            below-threshold decisions. The final row confirms null income also does
            not override a below-threshold rejection.
            Open: Is the senior threshold inclusive (score ≥ 600 → APPROVED) or
            exclusive (score > 600 → APPROVED)? These tests assume inclusive.
            Note: hasStableIncome must be declared as Boolean (wrapper) in the
            method signature to support the null income case in both tables.
            """)
    @TableTest("""
            Scenario                                | Customer Age | Credit Score | Income Stable | Result?
            Standard, well above threshold          | 30           | 700          | true          | APPROVED
            Standard, just above threshold          | 30           | 651          | true          | APPROVED
            Standard, at threshold boundary         | 30           | 650          | {true, false} | REJECTED
            Standard, below threshold               | 30           | 580          | {true, false} | REJECTED
            Standard, below threshold, null income  | 30           | 580          |               | REJECTED
            Age boundary: 64 (standard) at score 600| 64           | 600          | {true, false} | REJECTED
            Age boundary: 65 (senior) at score 600  | 65           | 600          | true          | APPROVED
            Senior, well above senior threshold     | 70           | 700          | true          | APPROVED
            Senior, just below senior threshold     | 70           | 599          | {true, false} | REJECTED
            """)
    void shouldApproveOrRejectByAgeBasedCreditThreshold(
            int customerAge, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(customerAge, creditScore, hasStableIncome));
    }

    @DisplayName("Income stability affects outcome for above-threshold applicants")
    @Description("""
            Credit scores are set well above the applicable threshold to isolate
            income stability logic. Both standard and senior applicants are included
            to confirm the income rule applies uniformly regardless of age group.
            """)
    @TableTest("""
            Scenario                            | Customer Age | Credit Score | Income Stable | Result?
            Standard applicant, stable income   | 30           | 700          | true          | APPROVED
            Standard applicant, unstable income | 30           | 700          | false         | REJECTED
            Standard applicant, missing income  | 30           | 700          |               | PENDING_REVIEW
            Senior applicant, stable income     | 70           | 630          | true          | APPROVED
            Senior applicant, unstable income   | 70           | 630          | false         | REJECTED
            Senior applicant, missing income    | 70           | 630          |               | PENDING_REVIEW
            """)
    void shouldHandleIncomeStatusForAboveThresholdApplicants(
            int customerAge, int creditScore, Boolean hasStableIncome, ApprovalResult result) {
        assertEquals(result, evaluator.evaluateLoan(customerAge, creditScore, hasStableIncome));
    }
}
```

---

Two things to act on before running:

1. **Method signature change** — `boolean hasStableIncome` (primitive) needs to become `Boolean hasStableIncome` (wrapper) to accept null. Blank cells in the table map to `null`, which a primitive can't receive.

2. **Open questions in the code** — two ambiguities are flagged in `@Description` on the first table:
   - Is the senior threshold at score 600 inclusive or exclusive? The `Age boundary: 65 at score 600 → APPROVED` row encodes the assumption; flip it to REJECTED if the rule is `> 600`.
   - When income is null *and* score is below threshold, which rule wins? The `Standard, below threshold, null income → REJECTED` row assumes threshold takes priority. If the intent is `PENDING_REVIEW`, move that row to the second table and change the result.