## Summary

Added `LoanEvaluatorTest.java` at `src/test/java/com/example/loan/` with one `@TableTest` covering `evaluateLoan(int customerAge, int creditScore, Boolean hasStableIncome)`, plus the `tabletest-junit` dependency in `build.gradle` (JUnit 6.0.3 already satisfies the 5.11+ requirement).

**Table design** (9 rows, 10 generated cases): treats this as one decision rule with three axes — age-tier threshold selection, the credit-score-vs-threshold boundary, and the income tri-state (true/false/null) — while avoiding redundant rows by only testing the income branch (unstable/missing) once for the age-independent behavior it represents, plus one extra row to directly verify stable income is still required for seniors (a genuine assumption, not just re-tested symmetry).

- Age boundary: 64 vs. 65 at the same score (601) — proves the cutoff is exactly 65, not the threshold value.
- Standard threshold (650) and senior threshold (600): each with an approve-above / reject-at pair.
- Missing income → `PENDING_REVIEW`, but only above the threshold — a below-threshold row confirms rejection wins regardless of income (`{true, false}` value set plus a blank-cell row for null).

Documented two interpretive assumptions in `@Description` since the spec doesn't explicitly restate them for seniors: the senior threshold is strict (`> 600`, not `>= 600`), and stable income is still required.

Left `LoanEvaluator` as the existing stub (throws `UnsupportedOperationException`) so it compiles but doesn't implement logic yet, per the request. Verified with `gradle compileTestJava` (clean) and `gradle test` (all 10 generated cases fail only due to the stub, confirming the table itself is correctly wired).