# Eval Review — tabletest variant=reference, Iteration 13

**Model:** sonnet · **Grading:** claude-sonnet-5 · **Date:** 2026-08-16 · **Evals:** 1

## Summary

12/12 (100.0%) · 0 tokens · 0.0s

> ⛔ **Void comparison — none of the 1 evals could be compared** vs iteration 12. Every delta below is computed over nothing; an absence of movement here is not evidence that nothing moved. See `analysis-todo.md`.

## Delta vs Iteration 12

**Not comparable (1) — excluded from the deltas above:**
- ⚠️ eval-7-permission-check: iteration 12 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 12

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-7-permission-check | 12/12 | — | 0 | — | 0.0 | — |

## Per-Eval Results

### ✅ Eval eval-7-permission-check

**12/12** · 0 tokens · 0ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions — rather than one row per role/action combination
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows (i.e. does not enumerate every role/action combination individually)
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output — forces value set consolidation (e.g. USER can READ and USER can WRITE, both true, should be one row with {READ, WRITE}).
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'permissionsByRoleAndAction' → 'Permissions By Role And Action'). Not a generic name like 'test1' or 'canPerform'.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ❌ **scenario-names-describe-conditions**: Scenario names name the variation the row exercises, not the result it produces. The decidable test: FAILS when a scenario name states or paraphrases a value that appears in an expectation column of that same row — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is a name echoing its own expectation cell, not a name that describes what the row is about, and not a name from which a reader who knows the rule could predict the outcome. A single offending name fails the assertion. Judge every @TableTest method in the class.
  > 'User, the action it is refused' and 'Guest, the actions it is refused' paraphrase the Allowed?=false outcome
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

