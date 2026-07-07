All 13 generated test cases pass (build successful, no failures).

**Delivered:** `src/test/java/com/example/InsuranceEvaluatorTest.java` with three `@TableTest` methods, decomposed by concern:

1. **`autoApprovesRenewalsWithNoClaims`** — the `RENEWAL` + zero-claims short-circuit takes precedence over both the standard and senior approval paths; includes a contrast row showing one claim or a non-renewal type breaks the bypass.
2. **`rejectsApplicationsAboveRiskThreshold`** — the risk-score (`age/10 + claimCount*15`) rejection cutoff at 75, with boundary rows at exactly 75 (approved) and 76 (rejected), plus a value-set row showing applicant type doesn't affect this decision.
3. **`calculatesPremiumByAgeTier`** — the senior (age ≥ 65) vs. standard premium formula switch, with claim count held fixed across the age-65 boundary rows to isolate the tier change.

Also added the `org.tabletest:tabletest-junit:1.2.1` dependency to `build.gradle` (it was missing).

**Assumptions** (documented in each table's `@Description`): applicant type is irrelevant to the rejection and premium decisions once past the auto-approval check, so it's held fixed or exercised via value sets rather than fully cross-producted; `hasActivePolicy` is a private field with no observable effect on `evaluateApplication`'s return value, so it's untested (black-box).