All three tables pass. `src/test/java/com/example/InsuranceEvaluatorTest.java` covers `evaluateApplication` with three tables, each one rule:

1. **`bypassesUnderwritingForACleanRenewal`** — the `RENEWAL` + zero-claims auto-approval short-circuit (age varies via a value set since it's irrelevant here; both failing conditions fall through to normal evaluation).
2. **`rejectsApplicationsWhoseRiskScoreExceedsTheThreshold`** — the risk-score rejection boundary (75), straddled by varying age with claim count held fixed.
3. **`appliesSeniorPricingFromTheAgeThreshold`** — the senior-vs-standard premium formula switch at age 65.

Assumption documented in each `@Description`: `internalRiskScore` isn't exposed by the public API, so the boundary rows are reached via specific age/claim-count combinations rather than a directly-verifiable intermediate value — noted rather than left implicit, per the skill's guidance for a seam that can't be added. Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` since it wasn't already a dependency.