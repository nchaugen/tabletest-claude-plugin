# Eval Review — tabletest-swift variant=next, Iteration 3

**Model:** sonnet (claude-sonnet-5-5) · **Grading:** claude-sonnet-5-5 · **Date:** 2026-11-24 · **Evals:** 1

## Summary

12/12 (100.0%) · 168118 tokens · 53.9s · $0.2795

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

> ⚠️ **2 assertion verdicts moved** vs official (iterations 7, 6, 5, 4, 3, 2, 1 merged). These are deltas, not attributions: read each eval's `outputs/` and `narration.md` before explaining any of them, and do not start the next iteration until every entry in `analysis-todo.md` has a cause.

## Per-Eval Results

### ✅ Eval eval-38-washing-machine-cells

**12/12** · 168118 tokens · 53864ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest attribute
- ✅ **tabletest-function-in-a-type**: Every @TableTest function is declared inside a type (a struct, class, enum or an extension of one), not at file scope.
- ✅ **no-if-switch-in-method**: Test function body contains no if, guard or switch statement and no ternary
- ✅ **has-question-mark-column**: Table has at least one column name ending with '?' (e.g. 'Outcome?')
- ✅ **outcome-cell-spelled-as-swift**: The expected outcome is one cell that spells the WashOutcome case as Swift code does, such as runs(minutes: 120) or tooHot(limit: 40), because WashOutcome gained Codable (or Decodable) in Sources/. FAILS when the outcome is split into a case-name column and number columns that the body puts back together, when the body builds the expected outcome from other columns, or when the test target maps words to cases by hand.
- ✅ **fabric-converts-on-the-app-type**: The fabric converts from a cell because the app's Fabric enum gained a one-word conformance in Sources/ (CaseIterable, Codable, Decodable, or String raw values). FAILS when the test target turns a word into a Fabric itself: a switch, a dictionary, a test-local copy of the enum, or a hand-written conversion.
- ✅ **app-types-change-by-one-word**: The only changes to the app code under Sources/ are conformances added to type declarations; no field of WashProgram is made optional, loses its default, or changes type, and no initialiser or method is added. PASSES when Sources/ is unchanged apart from such conformances.
- ✅ **no-decoder-in-app-code**: No init(from:) is written into the app code under Sources/.
- ✅ **program-rows-state-only-what-they-set**: Each row states only the program settings that the rule under test varies; the other settings keep the app's defaults. Either the settings are columns and the body builds the WashProgram, or a map cell names only the settings it sets and a TableValueDecodable conformance in the test target reads it (for example with readingFields). FAILS when every row must spell all five settings to decode, or when a map cell is read by an init(from:) or a synthesised Decodable that the app was changed to allow.
- ✅ **each-rule-has-its-rows**: Each of the four rules of the doc comment has rows that show it: each fabric at its temperature limit and one step above it; the base time of each fabric and the 10-minute step for 10 °C; a prewash, an extra rinse, and both together; and a delayed start against no delay. FAILS when a rule has no row, or when a limit is shown on one side only.
- ✅ **compiles**: swift build --build-tests succeeds against the package scaffolding
- ✅ **tests-pass**: The tests pass when run against WashPlanner

## Variant vs Official (iterations 7, 6, 5, 4, 3, 2, 1 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-38-washing-machine-cells | 10/12 | 12/12 | 170654 | 168118 | -1% | $0.2991 | $0.2795 | -7% | 70.7s | 53.9s | -24% |
| **Totals (1 comparable)** | **10/12** | **12/12** | **170654** | **168118** | **-1%** | **$0.2991** | **$0.2795** | **-7%** | **70.7s** | **53.9s** | **-24%** |

**Comparable summary (1 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| next (iter 3) | 12/12 (100.0%) | 168118 | $0.2795 | 53.9s |
| official | 10/12 (83.3%) | 170654 | $0.2991 | 70.7s |
| **Δ** | | **-1%** | **-7%** | **-24%** |

### Per-Assertion Comparison

| Eval | Assertion | official | next |
|------|-----------|----------|------|
| eval-38-washing-machine-cells | outcome-cell-spelled-as-swift | ❌ | ✅ |
| eval-38-washing-machine-cells | program-rows-state-only-what-they-set | ❌ | ✅ |

No load-bearing assertions found — variant matches official on all comparable assertions.

