Done. Summary:

- **`build.gradle`**: added `testImplementation 'org.tabletest:tabletest-junit:1.2.1'`.
- **`src/test/java/com/example/loan/LoanEvaluatorTest.java`**: two `@TableTest` methods against `LoanEvaluator.evaluateLoan`:
  - `evaluatesApprovalByAgeAdjustedThresholdAndIncome` — covers the age-adjusted threshold (650 standard / 600 for 65+), boundary rows at/above/below threshold, and stable-income true/false, including a `{true, false}` value set for "rejected regardless of income" rows.
  - `handlesUnknownIncomeInformation` — covers null `hasStableIncome`, showing `PENDING_REVIEW` when score meets the threshold and `REJECTED` when it doesn't (below-threshold rejection takes precedence over the missing-income rule).
- Documented two assumptions via `@Description` since the spec didn't state them explicitly: (1) sufficient score + confirmed unstable income → `REJECTED`; (2) below-threshold rejection overrides the missing-income rule even when income is null.
- `LoanEvaluator`/`ApprovalResult` left untouched (stub still throws `UnsupportedOperationException`).
- Verified: `gradle compileTestJava` succeeds; `gradle test` runs 18 generated cases, all failing with `UnsupportedOperationException` as expected pre-implementation.