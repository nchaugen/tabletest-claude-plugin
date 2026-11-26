# Eval Review — tabletest-swift variant=next, Iteration 6

**Model:** sonnet (claude-sonnet-5-5) · **Grading:** claude-sonnet-5-5 · **Date:** 2026-11-26 · **Evals:** 2

## Summary

24/24 (100.0%) · 527557 tokens · 109.2s · $0.4719

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **1 assertion verdict moved** vs official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged). These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Delta vs Iteration 5

**Improvements (1):**
- ✅ eval-44-ballot-box-replacement: `no-branch-on-table-enum-outside-it`

## Resource Comparison vs Iteration 5

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-42-dog-daycare-admission | 12/12 | 12/12 | 86410 | 107763 | 17.4 | 19.3 |
| eval-44-ballot-box-replacement | 12/12 | 11/12 | 441147 | 361883 | 91.8 | 64.8 |

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

### ✅ Eval eval-44-ballot-box-replacement

**12/12** · 441147 tokens · 91777ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **no-default-in-test-function**: No @TableTest function body defaults a value with ??, or calls .map, .flatMap or .compactMap on one of its parameters.
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Result?')
- ✅ **no-branch-on-table-enum-outside-it**: A switch, an if or guard that compares with a case, or a ternary on a case, of a test-local enum with String raw values is in a member of that enum (the enum itself or an extension of it), never in another function of the test type that takes the enum or reads a property of its type.
- ✅ **each-refusal-has-a-row**: Each of the three refusals has its own row, or its own value-set member, with its reason in an expectation cell: the poll is closed (pollClosed), the voter has no vote in the box (noVoteToReplace), the voter's ballot is sealed (ballotSealed). FAILS when a reason has no case.
- ✅ **unchanged-state-asserted**: A refused case asserts the state of the box after the call — the voter's vote (vote(of:)) and the tally — read from the box, in addition to the returned refusal. FAILS when a refused case asserts only the returned value.
- ✅ **unchanged-state-has-a-contrast**: The state after a refusal is asserted in one of three forms: a table that also has a row where the state changes (the accepted replacement); one row with a value set of the refusal reasons; or a before-state column whose cells differ between the refused rows, so that the after-state column differs too. FAILS when an after-state column (the voter's vote or the tally after the call) has the same cell in every row of a table with two or more rows, and when the unchanged state is asserted with a constant in the test body.
- ✅ **refusal-as-data**: The refusal reason is an expectation cell (such as refused(pollClosed), or a Refusal column), and the replaced and refused rows share one assertion. FAILS when the body chooses what to assert by a branch, or when the refusal is a constant in the body rather than a cell.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding

## Variant vs Official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-42-dog-daycare-admission | 12/12 | 12/12 | 110407 | 86410 | -22% | $0.1305 | $0.0815 | -38% | 24.3s | 17.4s | -28% |
| eval-44-ballot-box-replacement | 11/12 | 12/12 | 279005 | 441147 | +58% | $0.2693 | $0.3905 | +45% | 46.5s | 91.8s | +97% |
| **Totals (2 comparable)** | **23/24** | **24/24** | **389412** | **527557** | **+35%** | **$0.3998** | **$0.4719** | **+18%** | **70.8s** | **109.2s** | **+54%** |

**Comparable summary (2 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 6) | 24/24 (100.0%) | 527557 | $0.4719 | 109.2s |
| official | 23/24 (95.8%) | 389412 | $0.3998 | 70.8s |
| **Δ** | | **+35%** | **+18%** | **+54%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-44-ballot-box-replacement | no-branch-on-table-enum-outside-it | ❌ | ✅ |

No load-bearing assertions found — variant matches official on all comparable assertions.

