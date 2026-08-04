I wrote a single `@TableTest` for `evaluateApplication` in `src/test/java/com/example/InsuranceEvaluatorTest.java` (and added the `tabletest-junit` dependency to `build.gradle`).

**Design rationale:** The method is one decision rule with precedence — no-claims renewal auto-approval short-circuits the risk rejection, which in turn short-circuits the age-based premium tier — so it's one table with `Decision?` and `Premium?` as its two observable outputs, rather than split tables. The private `internalRiskScore`/`hasActivePolicy` fields aren't exposed via getters, so they're black-boxed: I computed expected premiums directly from `age`/`claimCount` rather than adding an unobservable intermediate column.

Six rows cover the obligations:
- baseline auto-approval, and a precedence-proving row (age=760 forces a rejection-level risk score, but auto-approval still wins — explained via `@Description` since the reasoning behind that odd value isn't visible from the row alone)
- the risk-rejection boundary from both sides (score 75 vs 76), with `Applicant Type` as a `{NEW, RENEWAL}` value set since it's irrelevant once claims > 0
- the senior-premium age boundary from both sides (64 vs 65), holding risk score constant so only the tier switch is being observed

Verified the table parses (via the skill's format-check script) and ran `gradle test` — all 8 generated cases (6 rows, 2 expanded by the value set) pass.