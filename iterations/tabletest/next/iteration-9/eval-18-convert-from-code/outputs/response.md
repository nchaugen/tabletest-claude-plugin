All 7 rows pass (3 + 2 + 2).

Summary of what was delivered at `src/test/java/com/example/InsuranceEvaluatorTest.java`:

- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (JUnit Jupiter was already 6.0.3, satisfying the ≥5.11 requirement).
- Three `@TableTest` methods, each isolating one concern of `evaluateApplication`, all invoked only through the public method (the private helpers aren't touched):
  1. **`evaluatesRenewalShortcut`** — the `RENEWAL` + zero-claims auto-approval bypass, and the two ways it fails to trigger (a claim present, or wrong applicant type).
  2. **`evaluatesRiskRejectionThreshold`** — the risk-score-75 rejection boundary (75 approved, 76 rejected), using low ages so claim count drives the score.
  3. **`evaluatesPremiumByAgeTier`** — the age-65 senior-premium switch, using ages 64/65 which share the same risk score (21), isolating the formula switch from any score change.

Each table asserts both `decision` and `premium` from `EvaluationResult`. The internal `hasActivePolicy`/`internalRiskScore` fields aren't exposed by the API and don't affect output, so they weren't modeled as columns. Ran via `gradle test` — all 7 rows pass.