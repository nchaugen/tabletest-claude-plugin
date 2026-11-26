# Eval Review — tabletest-swift variant=next, Iteration 1

**Model:** sonnet (claude-sonnet-5-5) · **Grading:** claude-sonnet-5-5 · **Date:** 2026-11-26 · **Evals:** 1

## Summary

12/12 (100.0%) · 363924 tokens · 79.5s · $0.3806

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **1 assertion verdict moved** vs official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged). These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Per-Eval Results

### ✅ Eval eval-44-ballot-box-replacement

**12/12** · 363924 tokens · 79477ms

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
| eval-44-ballot-box-replacement | 11/12 | 12/12 | 279005 | 363924 | +30% | $0.2693 | $0.3806 | +41% | 46.5s | 79.5s | +71% |
| **Totals (1 comparable)** | **11/12** | **12/12** | **279005** | **363924** | **+30%** | **$0.2693** | **$0.3806** | **+41%** | **46.5s** | **79.5s** | **+71%** |

**Comparable summary (1 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 1) | 12/12 (100.0%) | 363924 | $0.3806 | 79.5s |
| official | 11/12 (91.7%) | 279005 | $0.2693 | 46.5s |
| **Δ** | | **+30%** | **+41%** | **+71%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-44-ballot-box-replacement | no-branch-on-table-enum-outside-it | ❌ | ✅ |

No load-bearing assertions found — variant matches official on all comparable assertions.

