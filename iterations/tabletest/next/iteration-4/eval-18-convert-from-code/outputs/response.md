I wrote `src/test/java/com/example/InsuranceEvaluatorTest.java` with three `@TableTest` methods, one per concern in `evaluateApplication`:

1. **`autoApprovesRenewalsWithNoClaimsRegardlessOfAge`** — the `RENEWAL` + zero-claims shortcut takes precedence over the age-based premium tiers (tested with a value set across ages 20–90), with falsifying rows showing a renewal *with* a claim and a non-renewal applicant both fall through to normal evaluation instead.
2. **`rejectsApplicationsAboveRiskThreshold`** — the risk-score cutoff (75), tested with an at/just-over boundary pair (age/claimCount combos landing exactly on 75 and 76), since the risk score itself is a private calculation and can't be exposed as a column.
3. **`calculatesPremiumBySeniorAgeTier`** — the standard vs. senior premium formulas, isolating the age-64-vs-65 boundary by holding the risk score constant (both produce risk score 6) so only the tier/rate changes.

Assumptions (documented via `@Description` in the test file since this was a non-interactive run):
- `hasActivePolicy` and `internalRiskScore` are internal fields never read back into the return value, so they're excluded — the table only covers the `EvaluationResult` observable output.
- The risk-score formula and the 75 threshold are internal constants with no entry point to vary, so boundary rows (not a threshold column) carry that information instead.

Added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'` to `build.gradle` (already cached locally). Ran `gradle test` — build succeeds, all rows pass, confirming the expected decisions/premiums are arithmetically correct.