# Eval Review — tabletest-swift variant=next, Iteration 1

**Model:** sonnet (claude-sonnet-5-5) · **Grading:** claude-sonnet-5-5 · **Date:** 2026-11-26 · **Evals:** 2

## Summary

23/23 (100.0%) · 480852 tokens · 83.9s · $0.3632

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **4 assertion verdicts moved** vs official (iterations 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 merged). These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Per-Eval Results

### ✅ Eval eval-45-train-delay-notice

**11/11** · 274686 tokens · 44451ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Text?')
- ✅ **rules-tested-on-the-decision**: A @TableTest calls DelayNotice.notice(for:), the function that decides the notice, and its rows hold both sides of the 5-minute and the 60-minute line (4 and 5, 59 and 60). FAILS when the boundaries are tested only through the text of the notice.
- ✅ **no-long-text-cell**: No cell of a @TableTest table is longer than 100 characters. The table holds the decision as data, and a sentence is asserted outside the table. FAILS on a single long cell, since one long cell makes the whole table wide.
- ✅ **constant-text-not-restated**: Outside the @TableTest tables, the tests hold at most one string literal longer than 100 characters. A person reads the wording, so a copy of a constant sentence only checks that the two copies agree. One sentence can be the wiring check. FAILS on a test for each sentence.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against DelayNotice

### ✅ Eval eval-46-library-loan-reminder

**12/12** · 206166 tokens · 39494ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Text?')
- ✅ **rules-tested-on-the-decision**: LoanReminder.text(for:) calls a function or a computed property of the app that decides the reminder, and a @TableTest calls that function with rows on both sides of the 3-day, the due-date and the 30-day line (4 and 3, 0 and -1, -29 and -30). FAILS when the decision stays inside text(for:), and when the new function is a copy that text(for:) does not call.
- ✅ **no-long-text-cell**: No cell of a @TableTest table is longer than 100 characters. The table holds the decision as data, and a reader reviews the wording. FAILS on a single long cell, since one long cell makes the whole table wide.
- ✅ **constant-text-not-restated**: Outside the @TableTest tables, the tests hold at most one string literal longer than 100 characters. A person reads the wording, so a copy of a constant sentence only checks that the two copies agree. One sentence can be the wiring check. FAILS on a test for each sentence.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against LoanReminder
- ✅ **verify-tests-pass**: The eval's own tests pass against the changed app code: text(for:) gives the same text as before on both sides of each line, also for a reserved item.

## Variant vs Official (iterations 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-45-train-delay-notice | 9/11 | 11/11 | 209500 | 274686 | +31% | $0.2451 | $0.1822 | -26% | 36.7s | 44.5s | +21% |
| eval-46-library-loan-reminder | 10/12 | 12/12 | 215883 | 206166 | -5% | $0.1939 | $0.1810 | -7% | 51.0s | 39.5s | -23% |
| **Totals (2 comparable)** | **19/23** | **23/23** | **425383** | **480852** | **+13%** | **$0.4389** | **$0.3632** | **-17%** | **87.7s** | **83.9s** | **-4%** |

**Comparable summary (2 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 1) | 23/23 (100.0%) | 480852 | $0.3632 | 83.9s |
| official | 19/23 (82.6%) | 425383 | $0.4389 | 87.7s |
| **Δ** | | **+13%** | **-17%** | **-4%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-45-train-delay-notice | rules-tested-on-the-decision | ❌ | ✅ |
| eval-45-train-delay-notice | no-long-text-cell | ❌ | ✅ |
| eval-46-library-loan-reminder | rules-tested-on-the-decision | ❌ | ✅ |
| eval-46-library-loan-reminder | no-long-text-cell | ❌ | ✅ |

No load-bearing assertions found — variant matches official on all comparable assertions.

