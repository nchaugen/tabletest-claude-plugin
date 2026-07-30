All 14 rows (across the 5 `@TableTest` methods) executed and passed.

## Summary

Added the `tabletest-junit` dependency to `build.gradle` and wrote `src/test/java/com/example/InsuranceEvaluatorTest.java` with 5 tables covering `evaluateApplication`'s distinct rules:

1. **`autoApprovesRenewalsWithNoClaims`** — the RENEWAL+zero-claims shortcut, including a row proving it overrides rejection even when risk score alone would exceed 75.
2. **`rejectsApplicationsAboveRiskThreshold`** — the risk-score-75 boundary (straddling pair: 75 approved, 76 rejected).
3. **`selectsPremiumFormulaByAge`** — the age-65 boundary that switches between standard/senior premium formulas (same risk score, different formula).
4. **`computesStandardPremiumFromRiskScore`** / 5. **`computesSeniorPremiumFromRiskScore`** — pure arithmetic tables isolating each premium formula.

Assumptions made (documented in `@Description` where relevant):
- `applicantType` held at `"NEW"` in tables 2–5 to avoid interaction with the auto-approval shortcut (tested separately in table 1).
- `hasActivePolicy`/`internalRiskScore` fields aren't tested directly — they're private and not exposed via `EvaluationResult`, so they're not observable through the public API.
- No test for `applicantType == null` (NPE) — the method doesn't validate input, and this looked like a latent gap rather than a designed rule, so I left it out rather than inventing new scope.

Ran `gradle test` — build succeeded, all 14 generated rows passed.