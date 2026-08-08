All 14 generated test cases pass, confirming the row count matches expectations (5 + 4 [2 rows × 2-value sets] + 4 = 14).

## Summary

Wrote `src/test/java/com/example/InsuranceEvaluatorTest.java` with three `@TableTest` methods, one per concern in `evaluateApplication`:

1. **`grantsAutomaticApprovalForClaimFreeRenewals`** — the renewal+zero-claims short-circuit, including boundary rows proving it overrides both the senior premium tier and the risk rejection (using an intentionally implausible age, since the code has no age validation — documented in `@Description`).
2. **`rejectsApplicationsAboveTheRiskThreshold`** — straddles the risk-score-75 rejection boundary using age/claim-count values chosen to land exactly on each side (the risk score itself isn't exposed by the public API, so this is noted in `@Description` rather than published as a column).
3. **`calculatesPremiumByAgeTier`** — straddles the age-65 senior/standard premium boundary at equal risk (age 64 vs 65, same score), plus one extra data point per tier to pin down the formula's multiplier.

Also added the missing `org.tabletest:tabletest-junit:1.2.2` test dependency to `build.gradle`. Ran `gradle test` — build succeeded, all 14 generated rows pass.