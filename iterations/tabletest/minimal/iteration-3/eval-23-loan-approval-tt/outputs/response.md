No Java project exists here — I'll write the `LoanApprovalTest` class directly.

The rules decompose into three distinct concerns:
1. Standard applicant threshold (score > 650 + stable income)
2. Senior threshold modification (score > 600 for age ≥ 65)
3. Null income → `PENDING_REVIEW` (with interaction: below-threshold still rejects even when income is null)

Note: the given signature uses primitive `boolean` for `hasStableIncome`, but `null` must be representable to test the missing-income rule. The third table uses `Boolean` (boxed) — the implementation will need to match.

```java
public class LoanApprovalTest {

    @TableTest("""
        Scenario                          | Age | Credit Score | Stable Income | Result?
        Above threshold with income       | 30  | 700          | true          | APPROVED
        Above threshold without income    | 30  | 700          | false         | REJECTED
        At threshold boundary, not above  | 30  | 650          | true          | REJECTED
        Just above threshold              | 30  | 651          | true          | APPROVED
        Below threshold with income       | 30  | 500          | true          | REJECTED
        Below threshold without income    | 30  | 500          | false         | REJECTED
        Age 64 just below senior cutoff   | 64  | 625          | true          | REJECTED
        """)
    void standardApplicantDecisions(int age, int creditScore, boolean stableIncome, ApprovalResult result) {
        assertEquals(result, evaluateLoan(age, creditScore, stableIncome));
    }

    @Description("""
        Applicants aged 65+ are evaluated against a lower threshold of 600 instead of 650.
        Scores in the 601–650 range are only approvable for seniors.
        """)
    @TableTest("""
        Scenario                           | Age | Credit Score | Stable Income | Result?
        Above standard threshold           | 65  | 700          | true          | APPROVED
        In senior-only range with income   | 70  | 625          | true          | APPROVED
        Age boundary exactly 65            | 65  | 625          | true          | APPROVED
        At senior threshold, not above     | 65  | 600          | true          | REJECTED
        Just above senior threshold        | 65  | 601          | true          | APPROVED
        Below senior threshold with income | 65  | 550          | true          | REJECTED
        Senior-only range without income   | 65  | 625          | false         | REJECTED
        """)
    void seniorApplicantDecisions(int age, int creditScore, boolean stableIncome, ApprovalResult result) {
        assertEquals(result, evaluateLoan(age, creditScore, stableIncome));
    }

    @Description("""
        Null income (missing information) produces PENDING_REVIEW, but only when the
        credit score meets the applicable threshold. Below-threshold scores are rejected
        regardless of income — including when income data is absent.
        Requires hasStableIncome to be declared Boolean (boxed) in the method signature.
        """)
    @TableTest("""
        Scenario                              | Age | Credit Score | Stable Income | Result?
        Standard applicant, above threshold   | 30  | 700          |               | PENDING_REVIEW
        Senior applicant, above threshold     | 65  | 625          |               | PENDING_REVIEW
        Standard applicant, below threshold   | 30  | 600          |               | REJECTED
        Senior applicant, below threshold     | 65  | 550          |               | REJECTED
        """)
    void missingIncomeDecisions(int age, int creditScore, Boolean stableIncome, ApprovalResult result) {
        assertEquals(result, evaluateLoan(age, creditScore, stableIncome));
    }
}
```

Key design decisions:
- **Age boundary (64 vs 65)** is in table 1 as a "just below senior cutoff" row — score 625 is rejected for a 64-year-old because it's below 650, but would be approved for a 65-year-old (table 2).
- **Score boundary rows** (exactly 650, exactly 600) test the "above" condition is strict, not ≥.
- **Table 3 is separate** because it changes the expected result type (`PENDING_REVIEW`) and requires a nullable parameter — the assertion logic differs from tables 1 and 2.
- **Null income below threshold** (`REJECTED`) is included to document the rule interaction explicitly.