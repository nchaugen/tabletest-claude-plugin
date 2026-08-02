All 11 rows are parsed and expand correctly (the value sets generate the expected sub-cases), and each fails only on the stub's `UnsupportedOperationException` — exactly what's expected before implementation.

Summary:
- Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle`.
- Added `src/test/java/com/example/loan/LoanEvaluatorTest.java` with one `@TableTest` covering: standard/senior threshold boundaries (650/600, strictly-above), rejection when income is known false despite a high score, rejection below threshold regardless of income (value set), and `PENDING_REVIEW` for missing income regardless of age/score (value sets on both).
- Documented two assumptions in `@Description`: null income always yields `PENDING_REVIEW` irrespective of score/age, and known-false income rejects even above threshold.
- Left `LoanEvaluator`/`ApprovalResult` untouched (stub still throws `UnsupportedOperationException`) — logic isn't implemented yet, per your instructions.

Next step would be implementing `evaluateLoan` to make these 11 cases pass.