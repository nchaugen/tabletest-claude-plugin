The table compiles and parses correctly — all 10 generated test cases fail with `UnsupportedOperationException` from the stub, exactly as expected since the decision logic isn't implemented yet. This confirms the table design and parameter conversion are correct.

The `LoanEvaluator` and `ApprovalResult` stub already compiles against these tests, so no changes are needed there per the instructions to leave the implementation unimplemented.

## Summary

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` and wrote `src/test/java/com/example/loan/LoanEvaluatorTest.java` with one `@TableTest` (`decidesLoanApprovalStatus`) covering:

- Regular-applicant threshold boundary (650 rejects, 651 approves)
- Senior threshold boundary (600 rejects, 601 approves)
- The age boundary itself (64 vs. 65, same score 601) to isolate the age-adjusted threshold
- Below-threshold rejection regardless of known income (value set `{true, false}`) and regardless of unknown income
- Sufficient score with unstable (`false`) vs. unknown (`null`) income

**Assumptions documented in `@Description`** (since the spec didn't fully pin these down):
- "Above 650"/"lower threshold of 600" means strictly greater than, not inclusive.
- Below-threshold rejection takes priority over `PENDING_REVIEW` even when income is unknown.
- Known unstable income (`false`) above the threshold rejects (not explicitly stated in the rules, but implied by contrast with the approval rule).

Ran `gradle test`: all 10 generated cases fail with `UnsupportedOperationException` from the stub, confirming the table parses, converts, and wires up correctly — ready for the decision logic to be implemented against it.