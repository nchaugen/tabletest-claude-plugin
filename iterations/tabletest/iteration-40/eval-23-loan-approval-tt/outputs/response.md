The tests compile and generate exactly the 12 expected scenarios (the value-set row correctly expands to 2 cases). All fail with `UnsupportedOperationException` as expected, since the evaluator remains an unimplemented stub.

## Summary

Wrote `src/test/java/com/example/loan/LoanEvaluatorTest.java` with one `@TableTest` covering the loan approval decision (11 rows → 12 test cases via a value-set row), plus added `org.tabletest:tabletest-junit:1.2.1` to `build.gradle`. `LoanEvaluator` and `ApprovalResult` are untouched — evaluator still throws `UnsupportedOperationException`, so all 12 tests currently fail as intended.

Coverage: standard threshold boundary (650/651), senior threshold boundary (600/601), senior age-cutoff boundary (64/65), unstable income at a qualifying score, missing (null) income at a qualifying score (both standard and senior tiers), and below-threshold rejection regardless of income (true/false via a value set, plus null separately).

Two ambiguities weren't fully specified in the rules, so I documented my interpretation in the test's `@Description` rather than blocking on it:
- "Rejected regardless of income" is read to include missing (null) income, not just true/false — a below-threshold score always rejects.
- `PENDING_REVIEW` is reserved strictly for null income; a qualifying score with explicitly `false` income is `REJECTED`, since the rule requires income to be stable for approval.

Verified with `gradle test` — compiles cleanly and all 12 cases fail on the stub as expected.