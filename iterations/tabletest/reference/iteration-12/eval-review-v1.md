# Eval Review — tabletest variant=reference, Iteration 12

**Model:** sonnet · **Grading:** claude-sonnet-5 · **Date:** 2026-08-16 · **Evals:** 1

## Summary

13/13 (100.0%) · 0 tokens · 0.0s

> ⛔ **Void comparison — none of the 1 evals could be compared** vs iteration 11. Every delta below is computed over nothing; an absence of movement here is not evidence that nothing moved. See `analysis-todo.md`.

## Delta vs Iteration 11

**Not comparable (1) — excluded from the deltas above:**
- ⚠️ eval-9-bonus-contractor-structure: iteration 11 did not run this eval; nothing to compare against

## Resource Comparison vs Iteration 11

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-9-bonus-contractor-structure | 13/13 | — | 0 | — | 0.0 | — |

## Per-Eval Results

### ✅ Eval eval-9-bonus-contractor-structure

**13/13** · 0 tokens · 0ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **contractor-uses-value-set**: CONTRACTOR row uses a value set for Department (e.g. {SALES, ENGINEERING}) to express 'regardless of department' — not a dummy placeholder like 'ANY' or 'N/A', and not enumerated as separate rows.
- ✅ **four-core-rules-covered**: The four level×department combinations (SENIOR+SALES, SENIOR+ENGINEERING, JUNIOR+SALES, JUNIOR+ENGINEERING) are all present in the table(s)
- ✅ **expects-bonus-percentage**: The expected output column contains bonus percentages (e.g. 15%, 12%, 0%) — not bonus amounts. The method returns a percentage, and the table should reflect that.
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case (e.g. 'bonusByLevelAndDepartment' → 'Bonus By Level And Department'). Not a generic name like 'test1' or 'testMethod'.
- ✅ **description-if-present-adds-information**: PASSES when no @Description is present anywhere in the class — omitting it is always acceptable, and this assertion NEVER penalises its absence. Do not fail it for a missing @Description, and do not treat a @DisplayName as a substitute. When a @Description IS present, it must provide context beyond what the table rows already express — such as how bonus rates are applied (e.g. as percentage or fixed amount), or open questions — and FAILS when it merely restates the column names or summarises what the rows already show.
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **scenario-names-describe-conditions**: Scenario names name the variation the row exercises, not the result it produces. The decidable test: FAILS when a scenario name states or paraphrases a value that appears in an expectation column of that same row — 'User cannot delete' beside an Allowed? cell of false, 'No discount applies' beside a Discount? cell of 0.00, 'Contractor gets no bonus' beside a Bonus? cell of 0. Also FAILS on generic labels ('Test 1', 'Test case 2'), which name no variation at all. PASSES otherwise. Naming the rule or the situation is correct even when it makes the outcome inferable — 'Delivery and pickup items always split', 'At the standard threshold, not above it', 'EU express, light package', 'Renewal with no claims', 'Missing name' all PASS. The failure this catches is a name echoing its own expectation cell, not a name that describes what the row is about, and not a name from which a reader who knows the rule could predict the outcome. A single offending name fails the assertion. Judge every @TableTest method in the class.
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Level', 'Department', 'Bonus %') — not implementation terms like 'employeeLevel', 'deptCode', 'result'.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

