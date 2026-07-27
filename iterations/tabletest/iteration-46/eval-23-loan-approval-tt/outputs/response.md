All 14 rows parsed correctly and generated the expected number of test cases (5 + 6 + 3), each failing solely on `UnsupportedOperationException` from the stub — exactly what's expected before implementation.

## Summary

Wrote `src/test/java/com/example/loan/LoanEvaluatorTest.java` with three `@TableTest` methods, decomposed by concern:

1. **`appliesAgeAdjustedCreditScoreThreshold`** — age-adjusted threshold boundaries (650 standard / 600 senior), income fixed to `true`, including the age-64-vs-65 boundary interaction.
2. **`decidesFromStableIncomeAboveThreshold`** — income handling (true/false/null → APPROVED/REJECTED/PENDING_REVIEW) once score clears the threshold, with age as a `{30, 70}` value set to show it applies the same to standard and senior applicants.
3. **`rejectsBelowThresholdRegardlessOfIncome`** — the precedence rule: a below-threshold score rejects outright even when income is `null`, rather than falling to `PENDING_REVIEW`.

Documented assumptions in each `@Description` (income fixed to `true` in table 1; `false` income treated as rejection just like a failed threshold, since only `null` triggers review; the threshold-vs-missing-income precedence in table 3), since the prompt didn't fully specify these interactions.

Added the missing `org.tabletest:tabletest-junit:1.2.2` dependency to `build.gradle` (JUnit Jupiter was already 6.0.3, well above the 5.11 minimum). Verified with `gradle compileTestJava` (compiles cleanly) and `gradle test` (all 14 generated cases fail only on the stub's `UnsupportedOperationException`, confirming table parsing and parameter binding are correct). `LoanEvaluator`/`ApprovalResult` remain unimplemented as requested.