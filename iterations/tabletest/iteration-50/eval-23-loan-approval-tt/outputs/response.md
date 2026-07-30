Done. Summary of what I added:

- **`build.gradle`**: added `org.tabletest:tabletest-junit:1.2.2` test dependency.
- **`src/test/java/com/example/loan/LoanEvaluatorTest.java`**: one `@TableTest` covering the full decision (age-adjusted threshold, credit score, income) with 8 rows / 10 generated cases:
  - standard applicant just above 650 → `APPROVED`
  - at the 650 threshold (income `{true, false}`, and separately unknown) → `REJECTED` — the "unknown income" row at the boundary shows the below-threshold rule overriding the missing-income rule
  - senior applicant at the 600 threshold → `REJECTED`
  - age-boundary pair (64 vs 65 at the same score 610) showing exactly when the lower threshold kicks in
  - qualifying score with `false`/unknown income → `REJECTED` / `PENDING_REVIEW`

Two ambiguities weren't specified in the rules, so I made assumptions and documented them in `@Description`:
1. A below-threshold score rejects even when income is unknown (the "regardless of income" rule wins over the missing-income rule).
2. A qualifying score with confirmed non-stable income (`false`, not `null`) is `REJECTED`, since the spec only defines `PENDING_REVIEW` for unknown income.

`LoanEvaluator` still throws `UnsupportedOperationException` as before — I left the approval logic unimplemented per your instructions. I verified with `gradle test` that all 10 cases compile and execute (failing only with the expected `UnsupportedOperationException`, confirming the table syntax and type conversions are correct), then cleaned up the generated `build/` directory.