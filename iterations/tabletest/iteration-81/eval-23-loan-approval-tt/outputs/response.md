Table is well-formed and tests compile/run as expected (14 cases failing on the stub's `UnsupportedOperationException`).

**Summary of what was delivered:**
- `build.gradle`: added `org.tabletest:tabletest-junit:1.2.2` test dependency.
- `src/test/java/com/example/loan/LoanEvaluatorTest.java`: one `@TableTest` covering the full decision (age tier → threshold, credit score vs. threshold, income stability), 12 rows / 14 cases, boundary-tested at both the standard (650) and senior (600) credit-score thresholds and at the age-65 senior cutoff.
- `LoanEvaluator`/`ApprovalResult` left untouched (still stubs), per the "tests first" instruction.

**Assumptions documented in the `@Description`** (since the spec didn't spell them out):
1. "Above" the threshold is strict (`>`); a score equal to the threshold is treated as below it and rejected.
2. When the score qualifies but income is confirmed **false** (not just unknown), the result is `REJECTED`, not `PENDING_REVIEW` — that status is reserved for a genuinely unknown (`null`) income value.

Next step would be implementing `evaluateLoan` to make these pass.