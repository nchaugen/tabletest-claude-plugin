# Narration — eval-23-loan-approval-tt

The agent's visible narration and file writes, in order, distilled from
`conversation.jsonl`. Thinking text is absent unless the model returns it in the
clear (Claude 5-family models encrypt it).

I'll use the tabletest skill to write these tests properly.

Now updating `build.gradle` to add the tabletest-junit dependency, then writing the test class.

**Edit ./build.gradle (edit)**

**Write ./src/test/java/com/example/loan/LoanEvaluatorTest.java (60 lines)**

Compiles cleanly. Let's confirm the tests actually run (they should fail/error since the evaluator is a stub — that's expected and correct at this stage).

All 11 rows expand correctly and fail with `UnsupportedOperationException` as expected — the stub is untouched, so tests compile and run but the logic isn't implemented yet.

## Summary

Wrote `src/test/java/com/example/loan/LoanEvaluatorTest.java` with three `@TableTest` methods, one per concern, all calling `evaluateLoan` directly:

1. **`seniorApplicantsGetALowerThreshold`** — age/threshold concern (income fixed at `true`). Boundary rows at 650/651 and 600/601, plus an age-64-vs-65 pair at the same score (601) that flips outcome, falsifying "score alone decides."
2. **`incomeStatusDeterminesOutcomeForAQualifyingScore`** — income effect concern (age fixed at 40, score fixed at qualifying 700). Covers true/false/null → APPROVED/REJECTED/PENDING_REVIEW.
3. **`belowThresholdScoreIsRejectedRegardlessOfIncome`** — the one precedence case worth its own table: below-threshold score with unknown income still rejects rather than going to PENDING_REVIEW, showing rule 4 outranks rule 3. Used a `{true, false}` value set for the two "known income doesn't matter" cases plus a separate null row.

Also added the `tabletest-junit:1.2.2` test dependency to `build.gradle` (JUnit Jupiter is already 6.0.3, which satisfies the ≥5.11 requirement).

Assumptions documented in `@Description` blocks:
- Standard threshold is 650, senior (65+) threshold is 600, both "strictly above."
- When score is below the applicable threshold, rejection takes precedence over the missing-income → PENDING_REVIEW rule (the spec's "regardless of income" wording was read as covering this precedence).

Verified with `gradle compileTestJava` (succeeds) and `gradle test` (all 11 generated cases fail with `UnsupportedOperationException` from the untouched stub, as expected pre-implementation).

