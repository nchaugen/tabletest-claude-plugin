5 rows + 6 rows = 11 data rows, plus 1 container test = 12. All pass, build succeeds.

I wrote `src/test/java/com/example/InsuranceEvaluatorTest.java` with two `@TableTest` methods covering `evaluateApplication`:

1. **`bypassesEvaluationForZeroClaimRenewals`** — the renewal short-circuit rule (`applicantType == "RENEWAL" && claimCount == 0`), including precedence over the senior-premium branch and an extreme-age row proving it overrides what would otherwise be a rejection, plus two rows showing each half of that AND condition alone isn't enough.
2. **`decidesOutcomeAndPremiumFromRiskAndAge`** — the risk-score/age evaluation path once the bypass doesn't fire: applicant type shown as irrelevant there, the risk-score rejection boundary (75/76) straddled, the senior-age boundary (64/65) straddled at equal risk score to isolate the formula change, and a second senior-premium data point to pin down its slope/intercept.

Assumptions documented in the `@Description` blocks:
- `internalRiskScore` is a private field never exposed via `EvaluationResult`, so it's not a table column — expected decisions/premiums are stated directly instead of reimplementing the formula as a visible intermediate.
- The `age = 800` row is a deliberately unrealistic value used only to prove the bypass is truly unconditional, since realistic ages can never produce a risk score above the rejection threshold when `claimCount == 0`.

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (it wasn't previously a dependency). Ran the suite with the local `gradle` (no wrapper present) — build succeeded, 11 data rows all passing.