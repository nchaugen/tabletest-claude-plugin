Compilation succeeded and all 23 rows were correctly generated and parsed — they fail only with the expected `UnsupportedOperationException` from the stub, confirming the table syntax, value sets, and type conversion all work correctly. This matches the requirement to leave the evaluator unimplemented.

## Summary

Wrote `src/test/java/com/example/loan/LoanEvaluatorTest.java` with three `@TableTest` methods, decomposed by concern:

1. **`appliesLowerThresholdForSeniorApplicants`** — which credit-score threshold applies by age (650 standard vs. 600 for 65+), with stable income fixed `true`. Boundary rows at 64/65 and at/above each threshold.
2. **`missingIncomeInformationYieldsPendingReview`** — null income always yields `PENDING_REVIEW`, using value sets over age and score to assert this holds regardless of either (9 generated cases).
3. **`evaluatesFinalLoanDecision`** — the combining table tying threshold and income status together into the final verdict, using a value set `{true, false}` to show below-threshold scores are rejected regardless of income.

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (JUnit Jupiter is already 6.0.3, well above the 5.11 minimum).

Assumptions (documented in `@Description` blocks):
- "Above" the threshold is strict (`>`), not inclusive — verified with boundary rows (e.g., score 650 → `REJECTED`, 651 → `APPROVED`).
- Age 65 itself already qualifies for the senior threshold.
- `hasStableIncome = false` is treated as `REJECTED`, not `PENDING_REVIEW` — only `null` triggers pending review.

All 23 generated test cases compile and run, currently failing only on the stub's `UnsupportedOperationException` as expected — the evaluator's decision logic is intentionally left unimplemented.