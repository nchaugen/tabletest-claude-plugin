# Eval Review — tabletest-swift, Iteration 15

**Model:** sonnet (claude-sonnet-5-5) · **Grading:** claude-sonnet-5-5 · **Date:** 2026-11-26 · **Evals:** 1

## Summary

10/12 (83.3%) · 277326 tokens · 42.3s · $0.2112

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⛔ **Void comparison — none of the 1 evals could be compared** vs iteration 14. Every delta below is computed over nothing; an absence of movement here is not evidence that nothing moved. See `analysis-todo.md`.

## Delta vs Iteration 14

**Not comparable (1) — excluded from the deltas above:**
- ⚠️ eval-46-library-loan-reminder: iteration 14 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 14

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-46-library-loan-reminder | 10/12 | — | 277326 | — | 42.3 | — |

## Per-Eval Results

### ⚠️ Eval eval-46-library-loan-reminder

**10/12** · 277326 tokens · 42310ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Text?')
- ❌ **rules-tested-on-the-decision**: LoanReminder.text(for:) calls a function or a computed property of the app that decides the reminder, and a @TableTest calls that function with rows on both sides of the 3-day, the due-date and the 30-day line (4 and 3, 0 and -1, -29 and -30). FAILS when the decision stays inside text(for:), and when the new function is a copy that text(for:) does not call.
  > text(for:) calls no function or property of the app: the decision has no seam
- ❌ **no-long-text-cell**: No cell of a @TableTest table is longer than 100 characters. The table holds the decision as data, and a reader reviews the wording. FAILS on a single long cell, since one long cell makes the whole table wide.
  > remindsAboutALoanOutsideTheLastDays, row 1: a cell of 121 characters; remindsAboutALoanOutsideTheLastDays, row 2: a cell of 128 characters; remindsAboutALoanOutsideTheLastDays, row 3: a cell of 128 characters
- ✅ **constant-text-not-restated**: Outside the @TableTest tables, the tests hold at most one string literal longer than 100 characters. A person reads the wording, so a copy of a constant sentence only checks that the two copies agree. One sentence can be the wiring check. FAILS on a test for each sentence.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against LoanReminder
- ✅ **verify-tests-pass**: The eval's own tests pass against the changed app code: text(for:) gives the same text as before on both sides of each line, also for a reserved item.

