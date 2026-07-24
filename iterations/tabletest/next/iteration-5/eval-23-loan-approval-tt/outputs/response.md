Delivered three `@TableTest` methods in `LoanEvaluatorTest`, decomposed by concern:

1. **`appliesAgeAdjustedCreditScoreThreshold`** — the threshold-by-age rule (>650 standard, >600 for 65+), with boundary rows at 650/651, 600/601, and an age-64 row proving the lower threshold doesn't kick in early. `hasStableIncome` held at `true`.
2. **`handlesIncomeStatus`** — isolates income status (true/false/null) with age/score fixed above the standard threshold.
3. **`belowThresholdScoreOutranksMissingIncome`** — a combining table for the one genuine precedence question: when a below-threshold score and missing income coincide, which rule wins.

Assumptions (documented in `@Description` on the relevant tables, since this is a non-interactive run):
- Confirmed unstable income (`false`, not `null`) with an otherwise-passing score results in `REJECTED` — not explicitly stated in the rules, but implied since approval requires income to be confirmed stable.
- "Below-threshold scores are rejected regardless of income" is read literally to include unknown (`null`) income — so a below-threshold score rejects even when income is missing, rather than deferring to `PENDING_REVIEW`.

Added `org.tabletest:tabletest-junit:1.2.2` to `build.gradle` (JUnit was already 6.0.3, which satisfies the ≥5.11 requirement). Ran `gradle test`: it compiles cleanly and all 10 generated cases fail with the stub's `UnsupportedOperationException`, confirming the table syntax and wiring are correct ahead of implementation. `LoanEvaluator`/`ApprovalResult` were left as stubs, per the request.