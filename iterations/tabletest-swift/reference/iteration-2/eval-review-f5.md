# Eval Review — tabletest-swift variant=reference, Iteration 2

**Model:** sonnet · **Grading:** claude-sonnet-5-5 · **Date:** 2026-11-26 · **Evals:** 1

## Summary

9/10 (90.0%) · 0 tokens · 0.0s

> ⛔ **Void comparison — none of the 1 evals could be compared** vs iteration 1. Every delta below is computed over nothing; an absence of movement here is not evidence that nothing moved. See `analysis-todo.md`.

## Delta vs Iteration 1

**Not comparable (1) — excluded from the deltas above:**
- ⚠️ eval-45-train-delay-notice: iteration 1 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 1

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-45-train-delay-notice | 9/10 | — | 0 | — | 0.0 | — |

## Per-Eval Results

### ⚠️ Eval eval-45-train-delay-notice

**9/10** · 0 tokens · 0ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Text?')
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against DelayNotice

