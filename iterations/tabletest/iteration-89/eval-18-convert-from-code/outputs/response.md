Confirmed — 8/8 cases pass, and the value set correctly expands into two labeled cases (`Age = 30`, `Age = 70`).

## Summary

Wrote `src/test/java/com/example/InsuranceEvaluatorTest.java` with three `@TableTest` methods, one per concern in `evaluateApplication`'s priority-branching logic:

1. **`autoApprovesRenewalApplicantsWithNoClaims`** — proves the `RENEWAL` + zero-claims rule requires both conditions (varying each independently breaks it) and overrides age-based pricing entirely (age varied via `{30, 70}` value set, including a senior-qualifying age, with the same outcome).
2. **`rejectsApplicationsExceedingTheRiskThreshold`** — straddles the internal risk-score rejection boundary (75 vs. 76) with age/claim-count pairs chosen so only that boundary moves.
3. **`pricesThePremiumByAgeTier`** — straddles the age-65 senior/standard pricing boundary, with age/claim-count values picked so the internal risk score is identical (36) on both sides, isolating the age-tier switch from any other variable.

Also added the missing `tabletest-junit:1.2.2` test dependency to `build.gradle` (JUnit Jupiter was already 6.0.3, well above the 5.11 minimum).

**Assumption**: `hasActivePolicy` is a private field never returned or exposed, so it's not observable via the public API and isn't exercised by these tests — documented implicitly by omission rather than tested as a black box would require.

Ran `gradle test`: all 8 generated cases pass (build succeeded).