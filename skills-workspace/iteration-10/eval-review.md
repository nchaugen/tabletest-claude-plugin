# Eval Review — Iteration 10

**Model:** sonnet · **Date:** 2026-03-29 · **Evals:** 3

## Summary

**with_skill:** 23/24 (95.8%) · 392030 tokens · 170.3s · $0.4149

## Delta vs Iteration 9

**Regressions (1):**
- ❌ eval-7-permission-check: `has-descriptive-title`

## Resource Comparison vs Iteration 9

| Eval | Pass Rate | Prev | Tokens | Prev | Time(s) | Prev |
|------|-----------|------|--------|------|---------|------|
| eval-2-parse-dates | 9/9 | 9/9 | 183759 | 224126 | 79.5 | 107.7 |
| eval-5-order-transitions | 5/5 | 5/5 | 67327 | 65116 | 57.3 | 35.1 |
| eval-7-permission-check | 9/10 | 10/10 | 140944 | 174669 | 33.4 | 32.8 |

## Per-Eval Results

### ✅ Eval eval-2-parse-dates [with_skill]

**9/9** · 183759 tokens · 79492ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **null-as-blank-cell**: Null input is represented as a blank cell (not the string 'null') in the table
- ✅ **exception-handled-cleanly**: The empty string / exception case is handled — either via a Throws? column, assertThrows inside the method, or a separate @TableTest
- ✅ **localdate-result-column**: There is a result column typed as LocalDate (or using a string representation that maps to LocalDate)
- ✅ **type-conversion-addressed**: The response addresses type conversion for non-standard date formats (slash format, short year) — mentions @TypeConverter or a converter method
- ✅ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title when converted from camelCase/snake_case
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express — such as where/when the rule applies, or open questions. Does NOT merely restate the column names or summarise what the rows show. It is acceptable to omit @Description if the table already conveys all relevant context
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest — not any other order
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block (triple-quoted string """), not string concatenation with +. Passes if @Description is absent

### ✅ Eval eval-5-order-transitions [with_skill]

**5/5** · 67327 tokens · 57342ms

- ✅ **produces-markdown-table**: Output contains at least one markdown table
- ✅ **cancellation-coverage**: Table covers cancellation rules — includes rows for states where cancellation is allowed and where it is not
- ✅ **return-window-addressed**: The 30-day return window rule is addressed — either as a threshold column, a separate table, or flagged as an open question
- ✅ **value-set-or-multiple-states**: Uses value sets {PENDING, CONFIRMED} or equivalent to express 'regardless of which starting state' for cases where a rule holds across states
- ✅ **output-column-has-question-mark**: At least one output column ends with '?'

### ⚠️ Eval eval-7-permission-check [with_skill]

**9/10** · 140944 tokens · 33448ms

- ✅ **has-tabletest-annotation**: Output contains a @TableTest annotation
- ✅ **uses-value-sets**: Table uses value-set syntax (curly braces like {READ, WRITE} or {READ, WRITE, DELETE}) to express rules that hold across multiple actions
- ✅ **fewer-than-nine-rows**: Table has fewer than 9 data rows
- ✅ **no-if-switch-in-method**: Test method body contains no if or switch statements
- ✅ **scenario-column-present**: Table has a scenario/description column as the leftmost column
- ✅ **no-duplicate-role-output**: No two rows share both the same Role value and the same boolean output
- ❌ **has-descriptive-title**: Test method has either a @DisplayName annotation or a method name that reads as a clear, descriptive title
  > Method named 'checkPermission' which converts to 'Check Permission' — this is similar in style to the explicitly bad example 'canPerform' (a generic verb-based permission check) and lacks the specificity of the good example 'Permissions By Role And Action' which describes what is actually being tested
- ✅ **description-if-present-adds-information**: If @Description is present, it provides context beyond what the table rows already express
- ✅ **annotation-order**: Annotations appear in order: @DisplayName (if present), @Description (if present), @TableTest
- ✅ **description-uses-textblock**: If @Description is present and the text is longer than a single short line, it uses a text block

