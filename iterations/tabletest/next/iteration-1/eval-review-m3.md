# Eval Review — tabletest variant=next, Iteration 1

**Model:** sonnet (claude-sonnet-5) · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-23 · **Evals:** 1

## Summary

18/21 (85.7%) · 635900 tokens · 124.5s · $0.5749

_Cost figures are Claude Code list-price estimates; actual billing may differ (e.g. promotional pricing). Timed-out evals score 0 with unrecorded token usage._

## Per-Eval Results

### ⚠️ Eval eval-23-loan-approval-tt

**18/21** · 635900 tokens · 124536ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ❌ **threshold-as-column**: Policy thresholds (650/600 credit scores) appear in a dedicated policy column (e.g. 'Credit threshold') separate from the applicant's actual credit score column. The reader sees both the threshold and the score, making the comparison explicit. Not just threshold values used as data in the score column.
  > No dedicated 'Credit threshold' column exists. Thresholds (650/600) appear only in @Description text and scenario names, not as table columns alongside Credit Score.
- ✅ **concrete-domain-values**: Cell values use concrete domain terms (e.g. '70', '650', 'APPROVED') — not abstract codes like 'CATEGORY_A' or '1'. Boolean true/false is acceptable for yes/no flags when the parameter type is boolean.
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **scenario-names-describe-conditions**: Scenario names describe conditions ('Senior at lower threshold', 'Missing income') — not outcomes ('Approved', 'Rejected').
- ✅ **business-language-columns**: Column names use domain/business language (e.g. 'Age', 'Credit score', 'Stable income', 'Decision?') — not code identifiers like 'customerAge', 'boolean'.
- ✅ **has-question-mark-column**: At least one output column name ends with '?' (e.g. 'Decision?' or 'Status?')
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order.
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case.
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent.
- ✅ **concerns-decomposed**: Multiple tables (or @TableTest methods) are used, each addressing a distinct concern — not one monolithic table mixing unrelated rules. Acceptable to use a single table only when the domain genuinely has a single concern.
- ✅ **minimal-rows-per-concern**: Each table has only the rows needed to express its concern's rules — no unnecessary permutations from combining concerns. Fewer rows per table is expected when concerns are properly separated.
- ✅ **separates-age-credit-income**: Age boundary policy, credit score categorisation, and income status are separated into distinct @TableTest methods, with a method combining these for the expected verdict
- ✅ **description-no-redundant-field-values**: Scenario names and @Description text do not restate values the table already shows. The assertion FAILS if either: (1) a scenario name contains a literal value that also appears in an input cell of the same row (e.g. scenario 'Age 35, score 700' beside Age and Credit score columns holding 35 and 700); or (2) @Description states a fixed value for an input that is already a column in that table. Otherwise it PASSES. Stating a constant that is NOT a column — a threshold, a policy figure, a value fixed in the method body — is correct and must not fail this assertion.
- ✅ **depth-stable-income-effect**: The effect of stable income on the outcome is visible from table rows, not merely implied. The assertion PASSES if some single @TableTest table contains two rows that differ in the stable-income column (or equivalent) while holding the credit score and age columns equal, and those two rows carry different expected decisions. It FAILS if no such row pair exists in any table — including when stable income appears only in scenario names, only in the @Description, or only in rows that also vary another input.
- ✅ **has-tabletest-dependency**: The build file (build.gradle) includes org.tabletest:tabletest-junit as a test dependency
- ✅ **rule-falsifiable-by-a-row**: Cell values make the rule falsifiable, not merely distinguishable. Judge each @TableTest table independently; the assertion FAILS if any table meets any of these conditions: (1) its expectation column holds the same value in every row; (2) in every row, each expectation cell is a verbatim copy of a cell in an input column of the same row; (3) an implementation that ignored the input columns entirely and returned a fixed value would satisfy every row. Otherwise the assertion PASSES. Do not fail a table merely because its values are simple or round — only these three conditions matter.
- ✅ **rule-statable-from-table**: A reader can state the rule from the table alone, without the test method body. The assertion FAILS if any of these hold for any @TableTest: (1) a value needed to predict the expectation (a threshold, rate, cutoff, multiplier, reference date) appears only in the method body or a field, and in neither a column, the @DisplayName, nor the @Description; (2) the operation applied to the inputs cannot be named from the column headers and cell values alone; (3) a helper method used in the assertion applies a comparison criterion (ordering, tolerance, subset matching) that is stated nowhere in the table, title, or description. Otherwise the assertion PASSES. Setup that merely constructs the objects named by the columns is not a failure.
- ✅ **title-states-system-behaviour**: Each @DisplayName (or the method name it falls back to) states what the code under test does, not an external fact it depends on. Apply the check mechanically: strike the system under test from the sentence — if it still reads as true, because it merely restates a regulation, standard, format, or domain fact, the title is about something else and fails.
- ✅ **compiles**: The generated test code compiles successfully against the project scaffolding

