Table parses cleanly and is already aligned. Build passes with all 11 generated cases green.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/InsuranceEvaluatorTest.java` with one `@TableTest` covering `evaluateApplication`:

- **Auto-approval bypass**: `RENEWAL` + 0 claims short-circuits everything (even a senior-tier age); varying either half of that conjunction on its own (claim ≠ 0, or type ≠ `RENEWAL`) shows it falls through to normal evaluation instead.
- **Risk-rejection boundary**: rows at the internal risk-score threshold and just past it (`APPROVED` vs `REJECTED`).
- **Premium tier boundary**: age 64 vs 65 with the same risk score, isolating standard vs. senior premium formulas.
- **Type has no other effect**: `{NEW, EXISTING}` value sets on non-bypass rows show applicant type doesn't matter except as the literal `"RENEWAL"` string paired with zero claims.
- **Null applicant type**: added as a row (with a `Throws?` column) rather than a separate `@Test`, per the skill's guidance for a lone null-input case.

Key design call, documented in `@Description`: `internalRiskScore` is a private field with no getter, so it can't be a column without reimplementing the formula in the test. Decision and premium are verified together per row instead of splitting risk-classification from premium-arithmetic into separate tables.

Verified with `gradle test` — 11 generated cases (value sets expand rows 3/6/7), all passing — and the skill's `format-table.sh` confirms the table parses correctly.