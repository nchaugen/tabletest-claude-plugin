# Eval Review — tabletest-swift, Iteration 12

**Model:** sonnet (claude-sonnet-5-5) · **Grading:** claude-sonnet-5-5 · **Date:** 2026-11-26 · **Evals:** 1

## Summary

12/12 (100.0%) · 86410 tokens · 17.4s · $0.0815

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Delta vs Iteration 11

No changes.

## Resource Comparison vs Iteration 11

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-42-dog-daycare-admission | 12/12 | 12/12 | 86410 | 107763 | 17.4 | 19.3 |

## Per-Eval Results

### ✅ Eval eval-42-dog-daycare-admission

**12/12** · 86410 tokens · 17449ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Decision?')
- ✅ **new-cases-are-rows-of-the-existing-table**: The cases of the new rule (a puppy under 16 weeks needs a second vaccination) are rows of the existing admission table, the one that already has the age and vaccination columns. FAILS when a new @TableTest or @Test function holds any of them, or when the admission table is split in two.
- ✅ **untouched-tables-unchanged**: The play-group table and the daily-fee table are byte-equal to the scaffold's.
- ✅ **sixteen-weeks-both-sides**: With one vaccination, the admission table shows both sides of the 16-week line: a dog under 16 weeks gives needsVaccination and a dog of 16 weeks gives admitted. Rows at 15 and 16 weeks pass, and so does a value set whose largest member is 15 beside a row at 16. FAILS when no row is at 16 weeks, or when no row or value-set member is at 15 weeks.
- ✅ **scenario-names-add-to-the-row**: Read each scenario name the answer added or changed in the admission table beside the input cells of its own row. A name passes when it says where its row stands against the rule: a boundary (the youngest age, the 16-week line), a span of weeks, or the dose the rule asks for. FAILS when every added or changed name only repeats the words or values of its own input cells, such as "15 weeks, one vaccination" beside 15 and 1, or when the answer removes the scenario column.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against Admission

