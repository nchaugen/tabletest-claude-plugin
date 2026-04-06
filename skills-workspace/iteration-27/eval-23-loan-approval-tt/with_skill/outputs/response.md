Here is the complete `LoanApprovalTest.java`:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanApprovalTest {

    private final LoanEvaluator evaluator = new LoanEvaluator();

    @DisplayName("Loan approval evaluation")
    @Description("""
            Standard threshold: credit score > 650 (i.e. ≥ 651) with stable income → APPROVED.
            Senior threshold (age ≥ 65): credit score ≥ 600 with stable income → APPROVED.
            Below-threshold score → REJECTED regardless of income (even null).

            Open questions:
            - The method signature declares `boolean hasStableIncome` as a primitive, but
              null income must be representable. Rows with null income assume the signature
              is updated to `Boolean hasStableIncome`; confirm this change with the team.
            - If income is null AND score is below threshold, which rule wins: REJECTED
              ("regardless of income" suggests REJECTED) or PENDING_REVIEW? The rows below
              apply null-income rows only to above-threshold scores. Confirm behaviour for
              the null-income + below-threshold combination before adding those rows.
            - Senior threshold "600" is assumed inclusive (score = 600 → APPROVED). Confirm
              whether the boundary is ≥ 600 or > 600.
            """)
    @TableTest("""
            Scenario                              | Customer Age | Credit Score | Has Stable Income? | Approval Status?
            Standard approval                     | 35           | 700          | true               | APPROVED
            Score just above standard threshold   | 35           | 651          | true               | APPROVED
            Score at standard threshold           | 35           | 650          | true               | REJECTED
            No income, score above threshold      | 35           | 700          | false              | REJECTED
            Below standard threshold              | 35           | 600          | {true, false}      | REJECTED
            Senior approval                       | 65           | 630          | true               | APPROVED
            Senior score at threshold             | 65           | 600          | true               | APPROVED
            Senior score just below threshold     | 65           | 599          | {true, false}      | REJECTED
            Senior without stable income          | 65           | 630          | false              | REJECTED
            Just below senior age, senior score   | 64           | 630          | true               | REJECTED
            Null income, above standard threshold | 35           | 700          |                    | PENDING_REVIEW
            Null income, senior above threshold   | 65           | 630          |                    | PENDING_REVIEW
            """)
    void shouldEvaluateLoanApproval(int customerAge, int creditScore, Boolean hasStableIncome,
                                    ApprovalResult approvalStatus) {
        assertEquals(approvalStatus, evaluator.evaluateLoan(customerAge, creditScore, hasStableIncome));
    }
}
```

---

**Key design decisions:**

**Boundaries:** Both thresholds are tested at the exact boundary, one above, and one below — the most likely places for off-by-one bugs. Specifically, score 650 vs 651 (standard) and 599 vs 600 (senior), and age 64 vs 65.

**"Regardless of income" → value sets:** The `{true, false}` value set on below-threshold rows explicitly asserts income is irrelevant when the score fails — more expressive than two separate rows that happen to show the same output.

**`Boolean` vs `boolean`:** The primitive `boolean` signature can't accept null. The test uses boxed `Boolean` and the `@Description` flags the mismatch for the team to resolve. Blank cells in the table map to `null`.

**Three open questions surfaced** that the requirements left ambiguous, all documented in `@Description` rather than silently resolved in the code.