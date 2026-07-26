I've written the TableTest and updated `build.gradle`, but I can't verify compilation — the Bash tool is failing session-wide with a sandbox permission error (`EPERM ... mkdir '/Users/nch/.claude/session-env/...'`), and a subagent hit the identical failure, so it's not something fixable from here. I manually re-checked the table syntax, column-to-parameter alignment, and enum values against the stub files, and I'm confident in the code, but you should run the build yourself to confirm.

**Changes:**
- `build.gradle`: added `testImplementation 'org.tabletest:tabletest-junit:1.2.2'` (JUnit Jupiter was already 6.0.3, which satisfies the ≥5.11 requirement).
- `src/test/java/com/example/loan/LoanEvaluatorTest.java` (new): one `@TableTest` covering `evaluateLoan(age, creditScore, hasStableIncome)`.

**Design decisions / assumptions (documented in the test's `@Description`):**
- Thresholds are exclusive — a score *at* 650 (or 600 for seniors) is "below threshold," not approved. This follows the spec's "above 650" wording; I applied the same exclusivity to the senior threshold for consistency since the spec didn't state it explicitly.
- "Senior applicants (65+)" is inclusive at 65 — tested with a boundary pair (age 64 vs. 65, same score) to prove the threshold switch.
- Precedence assumption: "below-threshold scores are rejected regardless of income" is read to include *unknown* income too, so a below-threshold score with null income is `REJECTED`, not `PENDING_REVIEW`. `PENDING_REVIEW` only triggers when the score clears the threshold but income is unknown.
- A cleared score with income known `false` (not null) rejects by elimination — not stated explicitly, but follows since approval needs stable income and `PENDING_REVIEW` is reserved for the unknown case.

The stub `LoanEvaluator` (throws `UnsupportedOperationException`) and `ApprovalResult` enum were already in place and needed no changes — the tests will fail/error until the decision logic is implemented, as intended.

Next step: please run `./gradlew compileTestJava` (or a full `test`) in your shell to confirm it compiles, since I couldn't execute it here.