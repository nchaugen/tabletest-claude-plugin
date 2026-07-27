All 13 generated test cases pass (11 table rows, with the two value-set rows in the age-bracket table each expanding to 2 cases).

I added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and created `src/test/java/com/example/InsuranceEvaluatorTest.java` with three `@TableTest` methods, one per concern in `evaluateApplication`:

1. **Auto-approval precedence** — confirms `RENEWAL` + zero claims wins over the senior-premium branch, and that either condition alone isn't sufficient.
2. **Risk score rejection threshold** — boundary rows at risk score 75/76, plus a row showing rejection overrides senior-premium eligibility.
3. **Premium calculation by age bracket** — boundary rows at age 64/65 (same risk score, isolating the schedule switch), plus two rows confirming the standard/senior formulas scale correctly with risk score; applicant type is varied as a value set `{NEW, RENEWAL}` on the claims-bearing rows to show it doesn't affect premium once approved.

Assumption: `internalRiskScore`/`hasActivePolicy` are internal fields with no other observable effect, so tests only assert on the returned `EvaluationResult` (decision + premium) — documented via each table's `@Description` rather than tested directly.