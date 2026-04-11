Here's the complete `LoanApprovalTest.java`:

```java
import org.junit.jupiter.api.DisplayName;
import org.tabletest.junit.Description;
import org.tabletest.junit.TableTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanApprovalTest {

    private final LoanApprovalService service = new LoanApprovalService();

    @DisplayName("Loan approval decision")
    @Description("""
            Standard approval: credit score > 650 with stable income.
            Senior applicants (age >= 65): threshold lowered to 600, still requires stable income.
            Below-threshold scores are rejected regardless of stable or unstable income.
            Null income signals missing information and results in PENDING_REVIEW.

            Open: Is the senior threshold inclusive (>= 600) or exclusive (> 600)?
            'At senior threshold (600)' row assumes inclusive — revisit if the implementation
            treats it as exclusive.

            Open: Null income with a below-threshold credit score — does PENDING_REVIEW or
            REJECTED take precedence? Not covered until this is resolved.

            Note: The method signature must use Boolean (boxed) rather than boolean (primitive)
            to allow null values representing missing income information.
            """)
    @TableTest("""
            Scenario                                   | Age | Credit Score | Has Stable Income | Status?
            Standard approval                          | 30  | 700          | true              | APPROVED
            Just above standard threshold              | 30  | 651          | true              | APPROVED
            At standard threshold, not above           | 30  | 650          | true              | REJECTED
            Below standard threshold regardless        | 30  | 600          | {true, false}     | REJECTED
            Above threshold, no stable income          | 30  | 700          | false             | REJECTED
            Age 64 falls under standard threshold      | 64  | 630          | true              | REJECTED
            Age 65 qualifies for senior threshold      | 65  | 630          | true              | APPROVED
            Senior just above lower threshold          | 65  | 601          | true              | APPROVED
            At senior threshold (600)                  | 65  | 600          | true              | APPROVED
            Senior below lower threshold regardless    | 65  | 590          | {true, false}     | REJECTED
            Senior above threshold, no stable income   | 65  | 650          | false             | REJECTED
            Missing income regardless of score         | 30  | {700, 620}   | null              | PENDING_REVIEW
            Senior - missing income                    | 65  | {650, 580}   | null              | PENDING_REVIEW
            """)
    void shouldEvaluateLoan(int customerAge, int creditScore, Boolean hasStableIncome,
                            ApprovalResult expectedStatus) {
        assertEquals(expectedStatus, service.evaluateLoan(customerAge, creditScore, hasStableIncome));
    }
}
```

**Key design decisions:**

- **Age boundary pair** — rows at age 64 and 65 with score 630 (between both thresholds) make the senior cutoff concrete and verifiable: the same score flips from REJECTED to APPROVED.
- **`{true, false}` value sets** — for below-threshold rows, expresses "rejected regardless of income" declaratively. Applied to both standard and senior cases.
- **`{700, 620}` and `{650, 580}` value sets** — for PENDING_REVIEW rows, assert null income triggers the status regardless of whether the score would otherwise qualify or not.
- **`Boolean` instead of `boolean`** — two open questions surfaced in `@Description`: the senior threshold ambiguity (≥600 vs >600), and the rule conflict when both null income and below-threshold apply simultaneously. The 13 rows cover everything unambiguous.