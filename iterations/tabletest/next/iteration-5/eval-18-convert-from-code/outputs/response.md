All tests pass — 12 generated cases (6 + 4 + 2) across 3 `@TableTest` methods covering the three concerns in `evaluateApplication`:

1. **`autoApprovesRenewalsWithNoClaims`** — renewal + zero claims short-circuits to `AUTO_APPROVED` regardless of age; contrast rows show both conditions (type and claim count) are required.
2. **`rejectsApplicationsAboveRiskThreshold`** — boundary pair (risk score 75 vs 76, via age 9 vs 10 with 5 claims) shows the rejection cutoff, with a value set proving applicant type doesn't matter.
3. **`selectsPremiumFormulaByAgeTier`** — age 64 vs 65 isolates the standard-vs-senior premium formula switch while holding the risk score constant (both give risk score 6 via integer division), so the only variable is which formula applies.

I added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (was missing) and wrote `src/test/java/com/example/InsuranceEvaluatorTest.java`. `checkPolicyDatabase`/`hasActivePolicy` were left untested since they don't affect the return value (dead code). Null/invalid `applicantType` isn't tested since the method doesn't guard against it and nothing in scope calls for that validation.