# Eval Review — tabletest-swift variant=next, Iteration 2

**Model:** sonnet (claude-sonnet-5-5) · **Grading:** claude-sonnet-5-5 · **Date:** 2026-11-26 · **Evals:** 1

## Summary

9/12 (75.0%) · 306867 tokens · 54.4s · $0.3138

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **2 assertion verdicts moved** vs official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged). These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Delta vs Iteration 1

**Not comparable (1) — excluded from the deltas above:**
- ⚠️ eval-39-playlist-screen-columns: iteration 1 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 1

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-39-playlist-screen-columns | 9/12 | — | 306867 | — | 54.4 | — |

## Per-Eval Results

### ⚠️ Eval eval-39-playlist-screen-columns

**9/12** · 306867 tokens · 54376ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Actions?')
- ❌ **edit-columns-are-the-deciding-inputs**: For a saved playlist, the input columns are the inputs the screen reads to decide the actions: the unread rules and the change the user made to the rules (such as 'adds a rule' or 'removes every rule'), with test code that builds the query on screen from the change. FAILS when the columns hold full rule sets or queries, saved and on screen, that the reader must compare to find the change, or when a scenario name states a deciding input that no column holds.
  > Columns are "Opened From" (a="a playlist with an unread rule") and "Change"; the unread-rules count is folded into Opened From
- ❌ **irrelevant-input-is-a-value-set**: An input that does not change the actions is shown once, as a value set ({...}) or left out of the table with the fixture stating it: the order on the library, and the count of unread rules beyond zero. FAILS when two rows differ only in such an input and have the same expectations.
  > Library rows "Library as it opens" and "Library sorted differently" both have [] and differ only by sort order
- ❌ **crossing-the-inputs-finds-missing-rows**: The playlist table has a row for a playlist with an unread rule whose every rule is removed (actions: revert), which the original cases lacked.
  > No row pairs "a playlist with an unread rule" with "removes every rule"; response says "has no row"
- ✅ **rule-cell-names-only-what-it-sets**: Where a cell holds a rule set, it names only the rules it sets (such as [:] or [genres: {rock}]) and a TableValueDecodable conformance on SmartRules in the test target reads it, for example with readingFields. PASSES when no cell holds a rule set (the rules are columns and the body builds SmartRules). FAILS when every rule cell must name all three fields, or when the app was changed so a synthesised decoder accepts the cell.
- ✅ **action-list-converts-on-the-app-type**: The actions list cell converts because PlaylistScreen.Action gained a one-word conformance in Sources/ (CaseIterable, Codable, Decodable, or String raw values). FAILS when the test target turns words into actions itself (a switch, a dictionary, a test-local copy of the enum), or when the expectation is not a list of actions.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against PlaylistScreen

## Variant vs Official (iterations 9, 8, 7, 6, 5, 4, 3, 2, 1 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-39-playlist-screen-columns | 11/12 | 9/12 | 356426 | 306867 | -14% | $0.3062 | $0.3138 | +2% | 61.4s | 54.4s | -11% |
| **Totals (1 comparable)** | **11/12** | **9/12** | **356426** | **306867** | **-14%** | **$0.3062** | **$0.3138** | **+2%** | **61.4s** | **54.4s** | **-11%** |

**Comparable summary (1 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 2) | 9/12 (75.0%) | 306867 | $0.3138 | 54.4s |
| official | 11/12 (91.7%) | 356426 | $0.3062 | 61.4s |
| **Δ** | | **-14%** | **+2%** | **-11%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-39-playlist-screen-columns | edit-columns-are-the-deciding-inputs | ✅ | ❌ |
| eval-39-playlist-screen-columns | crossing-the-inputs-finds-missing-rows | ✅ | ❌ |

### Load-Bearing Assertions

Assertions that pass with official skill but fail with next variant:

- eval-39-playlist-screen-columns: `edit-columns-are-the-deciding-inputs`
- eval-39-playlist-screen-columns: `crossing-the-inputs-finds-missing-rows`

### Attention Needed

Evals with failed assertions or disproportionate resource usage (>2x official):

| Eval | Issue | Details |
|------|-------|---------|
| eval-39-playlist-screen-columns | Failed assertions | `edit-columns-are-the-deciding-inputs`, `crossing-the-inputs-finds-missing-rows` |

